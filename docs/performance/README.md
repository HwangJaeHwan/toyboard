# Toyboard Performance Test Summary

## Test Data

- Users: 1,000
- Posts: 100,000
- Comments: 300,000
- Recommendations: 100,000
- Elasticsearch `posts` documents: 100,000

## Main Optimizations

- Home popular posts: Redis ZSET ranks popular post ids by recent view activity.
- Post list: split one-shot aggregation into paged post lookup plus comment/recommendation count queries.
- Search: replace database `contains` search with Elasticsearch keyword search.
- MySQL indexes: add composite indexes for post type + sort columns and post-id indexes for count lookups.

## Final Indexed Result

Artillery was run against current endpoints at 1, 5, 10, 20, 30, 40, and 50 rps for 20 seconds per step.

| Area | 50 rps p95 | 50 rps p99 | Success |
| --- | ---: | ---: | ---: |
| Home | 28ms | 48ms | 100% |
| List | 36ms | 52ms | 100% |
| Search | 35.5ms | 52ms | 100% |

## Before/After Highlights

| Area | Scenario | Before p95 | After p95 |
| --- | --- | ---: | ---: |
| Home | Redis popular posts + indexed latest posts | 8610.2ms | 28ms |
| List | Split queries + MySQL indexes | 333.5ms | 36ms |
| Search | Elasticsearch search | 27ms | 35.5ms |

Note: the home before/after number includes more than Redis alone. The home page uses Redis for popular-post ranking, but still queries MySQL for popular-post hydration and latest posts. The final improvement also reflects MySQL indexing and test-environment warm-up effects.

## Reproduce

Run the Spring Boot app with MySQL, Redis, and Elasticsearch available, then run:

```powershell
npx artillery run -o reports/home-result.json docs/performance/artillery-home.yml
npx artillery run -o reports/list-result.json docs/performance/artillery-list.yml
npx artillery run -o reports/search-result.json docs/performance/artillery-search.yml
```

Generate HTML reports when needed:

```powershell
npx artillery report reports/home-result.json -o reports/home-report.html
npx artillery report reports/list-result.json -o reports/list-report.html
npx artillery report reports/search-result.json -o reports/search-report.html
```

