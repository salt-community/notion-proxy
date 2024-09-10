# Proxy API for Notion

This API provides information about consultants and their respective responsible people. Below are the available endpoints, the structure of their responses, and usage examples.

## Endpoints

### 1. `/api/responsible`
**Method**: `GET`

**Description**:  
Returns a list of all consultants, each with their respective responsible people.

**Response**:
```json
[
  {
    "name": "John Steven",
    "uuid": "860af135-00a8-422e-8eab-bffba946991f",
    "responsiblePersonList": [
      {
        "name": "Stålknapp",
        "uuid": "219b1e41-b7d2-4eee-bbaf-57d2248b77f7"
      },
      {
        "name": "Maggy",
        "uuid": "8b44b8f0-c1a5-46ab-a5c6-236d241cdb68"
      }
    ]
  },
  {
    "name": "Jane Doe",
    "uuid": "962f9af1-a9bf-4e28-b16b-335c5231e941",
    "responsiblePersonList": [
      {
        "name": "Markus",
        "uuid": "97a2b820-5b63-437d-917d-9f85c6a839a5"
      }
    ]
  }
]
```

### 2. `/api/responsible/{id}`
**Method**: `GET`

**Description**:  
Returns details of a specific consultant and their responsible people, identified by the consultant's `id`.

**Path Parameter**:
- `id` (string): The unique identifier (`uuid`) of the consultant.

**Response**:
```json
{
  "name": "John Steven",
  "uuid": "860af135-00a8-422e-8eab-bffba946991f",
  "responsiblePersonList": [
    {
      "name": "Stålknapp",
      "uuid": "219b1e41-b7d2-4eee-bbaf-57d2248b77f7"
    },
    {
      "name": "Maggy",
      "uuid": "8b44b8f0-c1a5-46ab-a5c6-236d241cdb68"
    }
  ]
}
```

## Usage Examples

1. **Get all consultants and their responsible people:**
   ```bash
   GET /api/responsible
   ```

   Response:
   ```json
   [
     {
       "name": "John Steven",
       "uuid": "860af135-00a8-422e-8eab-bffba946991f",
       "responsiblePersonList": [
         {
           "name": "Stålknapp",
           "uuid": "219b1e41-b7d2-4eee-bbaf-57d2248b77f7"
         },
         {
           "name": "Maggy",
           "uuid": "8b44b8f0-c1a5-46ab-a5c6-236d241cdb68"
         }
       ]
     },
     {
       "Name": "Jane Doe",
       "uuid": "962f9af1-a9bf-4e28-b16b-335c5231e941",
       "responsiblePersonList": [
         {
           "name": "Markus",
           "uuid": "97a2b820-5b63-437d-917d-9f85c6a839a5"
         }
       ]
     }
   ]
   ```

2. **Get a specific consultant by `id`:**
   ```bash
   GET /api/responsible/860af135-00a8-422e-8eab-bffba946991f
   ```

   Response:
   ```json
   {
     "name": "John Steven",
     "uuid": "860af135-00a8-422e-8eab-bffba946991f",
     "responsiblePersonList": [
       {
         "name": "Stålknapp",
         "uuid": "219b1e41-b7d2-4eee-bbaf-57d2248b77f7"
       },
       {
         "name": "Maggy",
         "uuid": "8b44b8f0-c1a5-46ab-a5c6-236d241cdb68"
       }
     ]
   }
   ```

## Error Handling

- If a consultant with the given `id` is not found, a `404 Not Found` status code will be returned.
- Invalid requests will return appropriate error codes (e.g., `400 Bad Request` for malformed requests).

## Response Codes

- `200 OK`: Request was successful, and the data is returned in the response body.
- `404 Not Found`: The requested resource (consultant or responsible person) was not found.
- `500 Internal Server Error`: There was an error processing the request.

---

This API is designed to provide easy access to consultant information and their responsible parties, streamlining data management and lookup for internal tools.