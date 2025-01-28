# Notion Proxy API

This API provides access to developer, assignment, and staff data. The details of the available endpoints are below.

## Base URL
All endpoints are prefixed with `/api`.

The API is deployed and can be accessed at: `https://notion-proxy-735865474111.europe-north1.run.app`

Swagger documentation: `https://notion-proxy-735865474111.europe-north1.run.app/swagger-ui/index.html`

## Authorization

All requests to this API must include an API key in the request header. The API key should be passed using the `X-API-KEY` header.
Currently, the only way to get an API key is to ask us directly.

## Endpoints

### 1. **`GET /api/developers`**
This returns a list of all developers with details such as name, email, and GitHub information.

**Query Parameters**:
- `status` (optional, default: `none`): A string filters results based on the status.
- `useCache` (optional, default: `true`): When `true`, use a cache up to 7 days old.

**Sample Response**:
```json
[
  {
    "name": "John Doe",
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "status": "active",
    "email": "johndoe@example.com",
    "githubUrl": "https://github.com/johndoe",
    "githubImageUrl": "https://avatars.githubusercontent.com/u/12345678?v=4",
    "totalScore": "85",
    "responsibles": [
      {
        "name": "Jane Smith",
        "id": "987e6543-e21b-34d3-a789-426614174111",
        "email": "janesmith@example.com"
      }
    ]
  }
]
```

---

### 2. **`GET /api/developers/{id}`**
This returns a developer by ID with details such as name, email, and GitHub information.

**Path Parameters**:
- `id` (string, UUID): The unique identifier for the developer.

**Query Parameters**:
- `useCache` (optional, default: `true`): When `true`, use a cache up to 7 days old.

**Sample Response**:
```json
{
    "name": "John Doe",
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "status": "active",
    "email": "johndoe@example.com",
    "githubUrl": "https://github.com/johndoe",
    "githubImageUrl": "https://avatars.githubusercontent.com/u/12345678?v=4",
    "totalScore": "85",
    "responsibles": [
      {
        "name": "Jane Smith",
        "id": "987e6543-e21b-34d3-a789-426614174111",
        "email": "janesmith@example.com"
      }
    ]
  }
```

---

### 3. **`GET /api/staff`**
This returns a list of all staff with details such as name, email, and role.

**Query Parameters**:
- `role` (optional, default: `none`): A string filters results based on the role.
- `useCache` (optional, default: `true`): When `true`, use a cache up to 7 days old.

**Sample Response**:
```json
[
  {
    "name": "Alice Johnson",
    "email": "alice.johnson@example.com",
    "staffId": "123e4567-e89b-12d3-a456-426614174000",
    "role": "P&T"
  }
]
```

---

### 4. **`GET /api/staff/{id}`**
This returns a staff by ID with details such as details such as name, email, and role.

**Path Parameters**:
- `id` (string, UUID): The unique identifier for the staff.

**Query Parameters**:
- `useCache` (optional, default: `true`): When `true`, use a cache up to 7 days old.

**Sample Response** (without consultants):
```json
[
  {
    "name": "Alice Johnson",
    "email": "alice.johnson@example.com",
    "staffId": "123e4567-e89b-12d3-a456-426614174000",
    "role": "P&T"
  }
]
```

---

### 5. **`GET /api/staff/{id}/consultants`**
Returns a list of consultants that the staff is responsible for by id.

**Path Parameters**:
- `id` (string, UUID): The unique identifier for the staff.

**Query Parameters**:
- `useCache` (optional, default: `true`): When `true`, use a cache up to 7 days old.

**Sample Response**:
```json
[
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "devId": "123e4567-e89b-12d3-a456-426614174000"
  }
]
```

---

### 6. **`GET /api/assignments`**
Retrieve all assignments associated with a specific developer by their unique developer ID

**Query Parameters**:
- `developerId ` (UUID, required): The unique identifier for the developer.
- `useCache` (optional, default: `true`): When `true`, use a cache up to 7 days old.

**Sample Response**:
```json
{
  "developerId": "123e4567-e89b-12d3-a456-426614174000",
  "assignments": [
    {
      "id": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
      "name": "Code Optimization",
      "score": 92,
      "categories": [
        "performance",
        "refactoring"
      ],
      "comment": "Great improvements in code efficiency and readability."
    }
  ]
}
```

---

### 6. **`GET /api/assignments/{id}`**
Retrieve details of a specific assignment by its unique ID

**Path Parameters**:
- `id` (string, UUID): The unique identifier for the assignment.

**Query Parameters**:
- `useCache` (optional, default: `true`): When `true`, use a cache up to 7 days old.

**Sample Response**:
```json
{
      "id": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
      "name": "Code Optimization",
      "score": 92,
      "categories": [
        "performance",
        "refactoring"
      ],
      "comment": "Great improvements in code efficiency and readability."
    }
```

---

## Error Handling

- **500 Internal Server Error**: Returned when there is an issue processing the request or if a request to notion failes.
- **404 Not Found**: Returned if the requested resource (developer, assignment, or staff) is not found.
- **400 Bad Request**: Returned if the provided parameters are invalid.

---
