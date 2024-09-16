# Notion Proxy API

This API provides access to data related to developers ("Salties"), consultants, and their responsible people, as well as developer scores. Below are the available endpoints, their usage, and sample responses.

## Base URL
The API is deployed and can be accessed at:

`https://notion-proxy-735865474111.europe-west4.run.app`

## Authorization

All requests to this API must include an API key in the request header. The API key should be passed using the `X-API-KEY` header.
Currently, the only way to get an API key is to be given one directly from us.

## Endpoints

### 1. `/api/notion`
**Method**: `GET`

**Description**:  
Returns a list of all developers (referred to as "Salties") with their basic details such as name, email, and GitHub information.

**Sample Response**:
```json
[
  {
    "name": "John Doe",
    "id": "860af135-00a8-422e-8eab-bffba946991f",
    "email": "john.doe@example.com",
    "githubUrl": "https://github.com/johndoe",
    "githubImageUrl": "https://github.com/johndoe.png"
  }
]
```

---

### 2. `/api/notion/responsible`
**Method**: `GET`

**Description**:  
Returns a list of consultants and their respective responsible people.

**Query Parameters**:
- `includeEmpty` (optional, default: `false`): When `true`, includes consultants who have no responsible people.
- `includeNull` (optional, default: `false`): When `true`, includes consultants with responsible people whose name is `null`.

**Sample Response**:
```json
[
  {
    "name": "Jane Smith",
    "id": "962f9af1-a9bf-4e28-b16b-335c5231e941",
    "responsiblePersonList": [
      {
        "name": "Markus",
        "id": "97a2b820-5b63-437d-917d-9f85c6a839a5"
      }
    ]
  }
]
```

---

### 3. `/api/notion/responsible/{id}`
**Method**: `GET`

**Description**:  
Returns the details of a single consultant, identified by their `id`, including the list of responsible people assigned to them.

**Path Parameters**:
- `id` (string, UUID): The unique identifier of the consultant.

**Query Parameters**:
- `includeNull` (optional, default: `false`): When `true`, includes consultants with `null` values in some fields.

**Sample Response**:
```json
{
  "name": "Jane Smith",
  "id": "962f9af1-a9bf-4e28-b16b-335c5231e941",
  "responsiblePersonList": [
    {
      "name": "Markus",
      "id": "97a2b820-5b63-437d-917d-9f85c6a839a5"
    }
  ]
}
```

---

### 4. `/api/notion/developer/{id}/score`
**Method**: `GET`

**Description**:  
Returns the scorecard of a developer, including their name, GitHub information, and a list of scores with categories.

**Path Parameters**:
- `id` (string, UUID): The unique identifier of the developer.

**Sample Response**:
```json
{
  "name": "John Doe",
  "id": "860af135-00a8-422e-8eab-bffba946991f",
  "githubUrl": "https://github.com/johndoe",
  "githubImageUrl": "https://github.com/johndoe.png",
  "email": "john.doe@example.com",
  "scores": [
    {
      "name": "Coding",
      "score": 95,
      "categories": ["Java", "Spring Boot", "REST APIs"]
    },
    {
      "name": "Problem Solving",
      "score": 85,
      "categories": ["frontend", "backend"]
    }
  ]
}
```

---

## Error Handling

- **500 Internal Server Error**: Returned when there is an issue processing the request.
- **404 Not Found**: Returned if the requested resource (consultant or developer) is not found.
- **400 Bad Request**: Returned if the provided parameters are invalid.

---

This API is designed to streamline access to data for internal tools and systems, providing clear endpoints for managing and retrieving information about developers, consultants, and their scores.