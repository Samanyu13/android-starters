# Problem: Retrieve Article Titles by Author

You are given access to a REST API that returns information about articles written by various authors.  
The API response is **paginated**, and you must fetch **all pages** to retrieve the complete result set.

---

## API Details

**Base URL**

https://testapi.com/api/articles


**Query Parameters**
- `author` (string): Name of the author to filter articles
- `page` (integer): Page number for paginated results (starts from 1)

**Example**

https://testapi.com/api/articles?author=epaga&page=1


---

## API Response Format

The API returns a JSON object with the following fields:

| Field Name    | Type    | Description |
|--------------|---------|-------------|
| `page`        | INTEGER | Current page number |
| `per_page`    | INTEGER | Maximum number of records per page |
| `total`       | INTEGER | Total number of records |
| `total_pages` | INTEGER | Total number of pages |
| `data`        | ARRAY   | List of article objects |

---

## Article Object Schema

Each element in the `data` array contains the following fields:

| Field Name     | Type   | Description |
|---------------|--------|-------------|
| `title`        | STRING | Title of the article |
| `url`          | STRING | URL of the article |
| `author`       | STRING | Author name |
| `num_comments` | LONG   | Number of comments |
| `story_id`     | LONG   | Unique article ID |
| `story_title`  | STRING | Alternate title for the article |
| `story_url`    | STRING | Alternate URL |
| `parent_id`    | LONG   | Parent article ID |
| `created_at`   | LONG   | Article creation timestamp |

---

## Task

Implement the following function:

```kotlin
fun getArticleTitles(author: String): Array<String>
```

## Function Requirements
1. Query the API using the given author parameter.
2. Handle pagination by fetching all pages (page = 1 to total_pages).
3. Create a list of article titles using the following rules:
    - If title is not null, use title.
    - If title is null and story_title is not null, use story_title.
    - If both are null, ignore the article.
7. Preserve the order in which articles are returned by the API.
8. Return the final result as an Array<String>.
