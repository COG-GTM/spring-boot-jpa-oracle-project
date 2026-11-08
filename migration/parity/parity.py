#!/usr/bin/env python3
"""HTTP parity harness for the /api/cars contract (stdlib only).

  run      replay scenario.json against a running app and write a normalized transcript
  compare  diff two transcripts (exit 1 on any mismatch)
  seed     load a deterministic synthetic dataset through the HTTP API
  snapshot GET /api/cars plus every /api/cars/{id} and write a normalized snapshot
"""
import argparse
import json
import random
import re
import sys
import urllib.error
import urllib.request

HASH_RE = re.compile(r"@[0-9a-f]{4,}")


def call(base, method, path, raw=None, content_type=None):
    data = raw.encode("utf-8") if raw is not None else None
    req = urllib.request.Request(base.rstrip("/") + path, data=data, method=method)
    if content_type:
        req.add_header("Content-Type", content_type)
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            return resp.status, dict(resp.headers), resp.read().decode("utf-8")
    except urllib.error.HTTPError as e:
        return e.code, dict(e.headers), e.read().decode("utf-8")


def normalize(status, headers, body):
    ctype = headers.get("Content-Type")
    parsed = None
    if body:
        try:
            parsed = json.loads(body)
        except ValueError:
            parsed = None
    if isinstance(parsed, dict) and "timestamp" in parsed and "status" in parsed:
        parsed = dict(parsed)
        parsed["timestamp"] = "<ignored>"
        if isinstance(parsed.get("message"), str):
            parsed["message"] = HASH_RE.sub("@<hash>", parsed["message"])
    return {
        "status": status,
        "contentType": ctype,
        "emptyBody": body == "",
        "body": parsed if parsed is not None else body,
    }


def cmd_run(args):
    scenario = json.load(open(args.scenario, encoding="utf-8"))
    out = []
    for step in scenario["steps"]:
        if "json" in step:
            raw, ctype = json.dumps(step["json"], ensure_ascii=False), "application/json"
        else:
            raw, ctype = step.get("raw"), step.get("contentType")
        if raw == "":
            raw = None
        res = normalize(*call(args.base_url, step["method"], step["path"], raw, ctype))
        res["name"] = step["name"]
        out.append(res)
    json.dump({"baseUrl": args.base_url, "steps": out}, open(args.out, "w", encoding="utf-8"), indent=2, ensure_ascii=False)
    print("recorded %d steps -> %s" % (len(out), args.out))


def cmd_compare(args):
    a = json.load(open(args.left, encoding="utf-8"))
    b = json.load(open(args.right, encoding="utf-8"))
    sa, sb = a["steps"], b["steps"]
    mismatches = []
    if len(sa) != len(sb):
        mismatches.append({"name": "<step count>", "left": len(sa), "right": len(sb)})
    for x, y in zip(sa, sb):
        diff = {k: {"left": x.get(k), "right": y.get(k)} for k in ("name", "status", "contentType", "emptyBody", "body") if x.get(k) != y.get(k)}
        if diff:
            mismatches.append({"name": x.get("name"), "diff": diff})
    total = min(len(sa), len(sb))
    report = {"left": args.left, "right": args.right, "steps": total, "matched": total - len([m for m in mismatches if m["name"] != "<step count>"]), "mismatches": mismatches}
    if args.out:
        json.dump(report, open(args.out, "w", encoding="utf-8"), indent=2, ensure_ascii=False)
    print("parity: %d/%d steps identical (%d mismatches)" % (report["matched"], total, len(mismatches)))
    for m in mismatches:
        print("  MISMATCH", json.dumps(m, ensure_ascii=False)[:400])
    return 1 if mismatches else 0


def synthetic_car(rng, i):
    brands = ["NORTHWIND", "CONTOSO", "FABRIKAM", "ACME", "INITECH", "\u0e42\u0e15\u0e42\u0e22\u0e15\u0e49\u0e32", "M\u00fcnchen", ""]

    def maybe(v):
        return None if rng.random() < 0.1 else v

    return {
        "carBrand": maybe(rng.choice(brands)),
        "carModel": maybe("MODEL-%d %s" % (i, "X" * rng.randint(0, 30))),
        "horsepower": maybe(str(rng.randint(60, 999999))),
        "carEngine": maybe(rng.choice(["1600", "2000", "2.0T", " 1.5 ", "EV", "\U0001F50B" * rng.randint(1, 6)])),
    }


def cmd_seed(args):
    rng = random.Random(args.seed)
    stats = {"posted": 0, "rejected": 0, "updated": 0, "deleted": 0}
    for i in range(args.count):
        car = synthetic_car(rng, i)
        if rng.random() < 0.03:
            car["carBrand"] = "Z" * 51
        status, _, _ = call(args.base_url, "POST", "/api/cars", json.dumps(car, ensure_ascii=False), "application/json")
        stats["posted" if status == 200 else "rejected"] += 1
        if i and rng.random() < 0.08:
            target = rng.randint(1, i)
            call(args.base_url, "PUT", "/api/cars/%d" % target, json.dumps(synthetic_car(rng, i), ensure_ascii=False), "application/json")
            stats["updated"] += 1
        if i and rng.random() < 0.05:
            call(args.base_url, "DELETE", "/api/cars/%d" % rng.randint(1, i))
            stats["deleted"] += 1
    print(json.dumps(stats))


def cmd_snapshot(args):
    status, headers, body = call(args.base_url, "GET", "/api/cars")
    rows = json.loads(body) if body else []
    ids = [int(next(iter(r))) for r in rows]
    by_id = {}
    for i in ids:
        s, h, b = call(args.base_url, "GET", "/api/cars/%d" % i)
        by_id[str(i)] = normalize(s, h, b)
    snap = {"list": normalize(status, headers, body), "byId": by_id, "count": len(ids)}
    json.dump(snap, open(args.out, "w", encoding="utf-8"), indent=2, ensure_ascii=False, sort_keys=True)
    print("snapshot: %d cars -> %s" % (len(ids), args.out))


def cmd_compare_snapshots(args):
    a = json.load(open(args.left, encoding="utf-8"))
    b = json.load(open(args.right, encoding="utf-8"))
    ids = sorted(set(a["byId"]) | set(b["byId"]), key=int)
    diffs = [i for i in ids if a["byId"].get(i) != b["byId"].get(i)]
    list_equal = a["list"] == b["list"]
    report = {"leftCount": a["count"], "rightCount": b["count"], "byIdCompared": len(ids), "byIdMismatches": diffs, "listResponseIdentical": list_equal}
    if args.out:
        json.dump(report, open(args.out, "w", encoding="utf-8"), indent=2)
    print("snapshot parity: list identical=%s, by-id %d/%d identical" % (list_equal, len(ids) - len(diffs), len(ids)))
    return 0 if list_equal and not diffs and a["count"] == b["count"] else 1


def main():
    p = argparse.ArgumentParser()
    sub = p.add_subparsers(dest="cmd")
    r = sub.add_parser("run"); r.add_argument("--base-url", required=True); r.add_argument("--scenario", default=__file__.replace("parity.py", "scenario.json")); r.add_argument("--out", required=True)
    c = sub.add_parser("compare"); c.add_argument("left"); c.add_argument("right"); c.add_argument("--out")
    s = sub.add_parser("seed"); s.add_argument("--base-url", required=True); s.add_argument("--count", type=int, default=500); s.add_argument("--seed", type=int, default=20261109)
    n = sub.add_parser("snapshot"); n.add_argument("--base-url", required=True); n.add_argument("--out", required=True)
    cs = sub.add_parser("compare-snapshots"); cs.add_argument("left"); cs.add_argument("right"); cs.add_argument("--out")
    args = p.parse_args()
    handlers = {"run": cmd_run, "compare": cmd_compare, "seed": cmd_seed, "snapshot": cmd_snapshot, "compare-snapshots": cmd_compare_snapshots}
    if args.cmd not in handlers:
        p.print_help(); return 2
    return handlers[args.cmd](args) or 0


if __name__ == "__main__":
    sys.exit(main())
