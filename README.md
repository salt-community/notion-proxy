# Notion Proxy API

This API provides access to developer, consultant, and responsible person data, including scores for developers and relationships between consultants and their responsible people. Below are the details of the available endpoints.

## Base URL
All endpoints are prefixed with `/api/salt`.

The API is deployed and can be accessed at: `https://notion-proxy-735865474111.europe-west4.run.app`

## Authorization

All requests to this API must include an API key in the request header. The API key should be passed using the `X-API-KEY` header.
Currently, the only way to get an API key is to ask us directly.

## Endpoints

### 1. **`GET /api/salt`**
Returns a list of all developers ("Salties") with their basic details such as name, email, and GitHub information.

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

### 2. **`GET /api/salt/consultants`**
Returns a list of consultants and their respective responsible people.

**Query Parameters**:
- `includeEmpty` (optional, default: `false`): When `true`, includes consultants without responsible people.
- `includeNull` (optional, default: `false`): When `true`, includes consultants with `null` values in some fields.

**Sample Response**:
```json
[
  {
    "name": "Jane Smith",
    "id": "962f9af1-a9bf-4e28-b16b-335c5231e941",
    "responsiblePersonList": [
      {
        "name": "Markus",
        "id": "97a2b820-5b63-437d-917d-9f85c6a839a5",
        "email": "markustest@gmail.com"
      }
    ]
  }
]
```

---

### 3. **`GET /api/salt/consultants/{id}`**
Returns a single consultant identified by their `id`, along with the responsible people assigned to them.

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
      "id": "97a2b820-5b63-437d-917d-9f85c6a839a5",
      "email": "markustest@gmail.com"
    }
  ]
}
```

---

### 4. **`GET /api/salt/responsible`**
Returns a list of responsible people. You can choose to include associated consultants or return a simplified list of responsible people only.

**Query Parameters**:
- `includeNull` (optional, default: `false`): When `true`, includes responsible people with `null` values.
- `includeConsultants` (optional, default: `false`): When `true`, includes consultants assigned to the responsible people.

**Sample Response** (without consultants):
```json
[
  {
    "name": "Markus",
    "id": "97a2b820-5b63-437d-917d-9f85c6a839a5",
    "email": "markustest@gmail.com"
  }
]
```

**Sample Response** (with consultants):
```json
[
  {
    "name": "Markus",
    "id": "97a2b820-5b63-437d-917d-9f85c6a839a5",
    "email": "markustest@gmail.com",
    "consultants": [
      {
        "name": "Jane Smith",
        "id": "962f9af1-a9bf-4e28-b16b-335c5231e941"
      }
    ]
  }
]
```

---

### 5. **`GET /api/salt/responsible/{id}`**
Returns a single responsible person by their `id`. You can choose to include or exclude the consultants associated with this responsible person.

**Path Parameters**:
- `id` (string, UUID): The unique identifier of the responsible person.

**Query Parameters**:
- `includeNull` (optional, default: `false`): When `true`, includes responsible people with `null` values.
- `includeConsultants` (optional, default: `false`): When `true`, includes consultants assigned to the responsible person.

**Sample Response** (without consultants):
```json
{
  "name": "Markus",
  "id": "97a2b820-5b63-437d-917d-9f85c6a839a5",
  "email": "markustest@gmail.com"
}
```

**Sample Response** (with consultants):
```json
{
  "name": "Markus",
  "id": "97a2b820-5b63-437d-917d-9f85c6a839a5",
  "email": "markustest@gmail.com",
  "consultants": [
    {
      "name": "Jane Smith",
      "id": "962f9af1-a9bf-4e28-b16b-335c5231e941"
    }
  ]
}
```

---

### 6. **`GET /api/salt/developers/{id}/scores`**
Returns the scorecard of a developer, including their name, email, GitHub information, and a list of scores along with categories.

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
      "score": 88,
      "categories": ["Algorithms", "Data Structures"]
    }
  ]
}
```

---

## Error Handling

- **500 Internal Server Error**: Returned when there is an issue processing the request.
- **404 Not Found**: Returned if the requested resource (consultant, responsible person, or developer) is not found.
- **400 Bad Request**: Returned if the provided parameters are invalid.

---

This API serves as a gateway to manage and retrieve data about developers, consultants, responsible people, and their relationships.