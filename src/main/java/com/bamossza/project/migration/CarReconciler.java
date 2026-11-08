package com.bamossza.project.migration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

import com.bamossza.project.entities.Car;

/** Compares two snapshots of the CAR data set. Pure function: no I/O. */
public final class CarReconciler {

    private CarReconciler() {
    }

    public static ReconciliationReport compare(String sourceName, SortedMap<Integer, Car> source,
            String targetName, SortedMap<Integer, Car> target) {
        ReconciliationReport report = new ReconciliationReport();
        report.sourceStore = sourceName;
        report.targetStore = targetName;
        report.sourceCount = source.size();
        report.targetCount = target.size();
        for (Map.Entry<Integer, Car> e : source.entrySet()) {
            Car other = target.get(e.getKey());
            if (other == null) {
                report.missingInTarget.add(e.getKey());
            } else if (!sameFields(e.getValue(), other)) {
                report.fieldMismatches.add(e.getKey());
            }
        }
        for (Integer id : target.keySet()) {
            if (!source.containsKey(id)) {
                report.extraInTarget.add(id);
            }
        }
        report.sourceChecksum = checksum(source);
        report.targetChecksum = checksum(target);
        return report;
    }

    public static boolean sameFields(Car a, Car b) {
        return Objects.equals(a.getCarId(), b.getCarId())
                && Objects.equals(a.getCarBrand(), b.getCarBrand())
                && Objects.equals(a.getCarModel(), b.getCarModel())
                && Objects.equals(a.getHorsepower(), b.getHorsepower())
                && Objects.equals(a.getCarEngine(), b.getCarEngine());
    }

    /** SHA-256 over every row in ID order; nulls are encoded distinctly from empty strings. */
    public static String checksum(SortedMap<Integer, Car> rows) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (Car car : rows.values()) {
                StringBuilder line = new StringBuilder().append(car.getCarId());
                for (String v : new String[] {car.getCarBrand(), car.getCarModel(), car.getHorsepower(), car.getCarEngine()}) {
                    line.append('\u001f').append(v == null ? "\u0000" : v.length() + ":" + v);
                }
                digest.update(line.append('\n').toString().getBytes(StandardCharsets.UTF_8));
            }
            StringBuilder hex = new StringBuilder();
            for (byte b : digest.digest()) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
