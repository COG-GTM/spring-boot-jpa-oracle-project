#!/usr/bin/env bash
# Replays the HTTP scenario against the built JAR in both profiles and diffs each transcript
# against the committed Oracle baseline (captured from the pre-migration code on Oracle XE 11g).
# usage: migration/parity/ci-parity.sh [path/to/car.jar] [out-dir]
set -euo pipefail
JAR=${1:-target/car-0.0.1-SNAPSHOT.jar}
OUT=${2:-target/parity}
HERE=$(cd "$(dirname "$0")" && pwd)
JAVA=${JAVA:-java}
mkdir -p "$OUT"

start() { # port log args...
  local port=$1 log=$2; shift 2
  "$JAVA" -Doracle.jdbc.timezoneAsRegion=false -jar "$JAR" --server.port="$port" "$@" > "$log" 2>&1 &
  echo $! > "$OUT/$port.pid"
  for _ in $(seq 1 90); do curl -s -o /dev/null "localhost:$port/api/cars" && return 0; sleep 2; done
  tail -50 "$log"; return 1
}
stop() { kill "$(cat "$OUT/$1.pid")" 2>/dev/null || true; sleep 3; }

status=0
start 8081 "$OUT/oracle.log" --spring.profiles.active=oracle
python3 "$HERE/parity.py" run --base-url http://localhost:8081 --out "$OUT/target-oracle-profile.json"
python3 "$HERE/parity.py" compare "$HERE/baseline/oracle-baseline.json" "$OUT/target-oracle-profile.json" --out "$OUT/parity-oracle-profile.json" || status=1
stop 8081

MONGO_URI=${MONGO_URI:-mongodb://localhost:27017/carsystem_parity_ci}
mongo_db=${MONGO_URI##*/}
if command -v mongosh >/dev/null; then mongosh --quiet "$MONGO_URI" --eval 'db.dropDatabase()' >/dev/null; fi
start 8082 "$OUT/mongodb.log" --spring.profiles.active=mongodb --spring.data.mongodb.uri="$MONGO_URI"
python3 "$HERE/parity.py" run --base-url http://localhost:8082 --out "$OUT/target-mongodb-profile.json"
python3 "$HERE/parity.py" compare "$HERE/baseline/oracle-baseline.json" "$OUT/target-mongodb-profile.json" --out "$OUT/parity-mongodb-profile.json" || status=1
stop 8082
echo "parity database used: $mongo_db (must start empty)"
exit $status
