# EnigWed API

Welcome to the EnigWed API! This API serves as the backend for EnigWed, an application designed to connect wedding organizers with customers seeking to hire wedding planning services.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Technologies Used](#technologies-used)
- [Contributing](#contributing)
- [License](#license)

## Features

- List available wedding organizers and their services
- Enable customers to search for and book wedding organizers
- Manage user accounts and profiles
- Handle inquiries and messages between customers and organizers

## Installation

To set up the EnigWed API locally, follow these steps:

1. Clone the repository:

```bash
git clone https://github.com/muhamadafrizalf/enigwed-api.git
cd enigwed-api
```

## Usage

## API Endpoints

#### Standard API Response

```json
{
  "success": true,
  "message": "string",
  "data": {},
  "paging": {
    "page": "integer",
    "size": "integer",
    "totalElements": "long",
    "totalPages": "integer"
  },
  "error": "string"
}
```

---

### Authentication API

#### Register Wedding Organizer

- Endpoint: `POST /api/auth/register`
- Request Body:

```json
{
  "name": "string",
  "description": "string",
  "address": "string",
  "npwp": "string",
  "nib": "string",
  "cityId": "string",
  "phone": "string",
  "email": "string",
  "password": "string",
  "confirmPassword": "string"
}
```

- Response:

    - Success:
  
        - **Status Code:** 200 OK
        - **Response Body:**
      
      ```json
      {
        "success": true,
        "message": "Register success"
      }
      ```
      
    - Failed:

        - **Status Code:** 400 Bad Request
        - **Response Body:**

      ```json
      {
        "success": false,
        "message": "Register failed",
        "error": "Required fields are missing"
      }
      ```

        - **Status Code:** 409 Conflict
        - **Response Body:**

      ```json
      {
        "success": false,
        "message": "Register failed",
        "error": "Email already in use"
      }
      ```

        - **Status Code:** 500 Internal Server Error
        - **Response Body:**

      ```json
      {
        "success": false,
        "message": "Register failed",
        "error": "An unexpected error occurred"
      }
      ```

### Login

- Endpoint: `POST /api/auth/login`
- Request Body:

```json
{
  "email": "string",
  "password": "string"
}
```

- Response:

    - Success:

        - **Status Code:** 200 OK
        - **Response Body:**

      ```json
      {
        "success": true,
        "message": "Login success",
        "data": {
          "token": "jwt_token",
          "role": "enum_role"
        }
      }
      ```

    - Failed:

        - **Status Code:** 400 Bad Request
        - **Response Body:**

      ```json
      {
        "success": false,
        "message": "Login failed",
        "error": "Email and password are required"
      }
      ```

        - **Status Code:** 401 Unauthorized
        - **Response Body:**

      ```json
      {
        "success": false,
        "message": "Login failed",
        "error": "Invalid email or password"
      }
      ```

        - **Status Code:** 500 Internal Server Error
        - **Response Body:**

      ```json
      {
        "success": false,
        "message": "Login failed",
        "error": "An unexpected error occurred"
      }
      ```

---

### City API

#### Index

- Endpoint: `GET /api/pubic/cities?`
- Query Param: {name}
- Response Body:

```json
{
  "data": [
    {
      "id": "",
      "name": "",
      "thumbnail": "resource",
      "deletedAt": "",
      "createdAt": "",
      "updatedAt": ""
    }
  ]
}
```

#### Read

- Endpoint: `GET /api/cities/:id`
- Response Body (Success):

```json
{
  "id": "",
  "name": "",
  "thumbnail": "",
  "deletedAt": "",
  "createdAt": "",
  "updatedAt": ""
}
```

#### Create

- Endpoint: `POST /api/cities`
- Request Body:

```json
{
  "name": "",
  "thumbnail": ""
}
```

- Response Body (Success):

```json
{
  "success": true,
  "message": "string"
}
```

#### Update

- Endpoint: `PUT /api/cities/:id`
- Request Body:

```json
{
  "name": "",
  "thumbnail": ""
}
```

- Response Body (Success):

```json
{
  "success": true,
  "message": "string"
}
```

#### Destroy

- Endpoint: `DELETE /api/cities/:id`
- Response Body (Success):

```json
{
  "success": true,
  "message": "string"
}
```

---

### Wedding Organizer API

#### Index

- Endpoint: `GET /api/wedding-organizers?`
- Query Param: {name, phone}
- Response Body (Success):

```json
{
  "data": [
    {
      "id": "",
      "name": "",
      "thumbnail": "",
      "phone": "",
      "npwp": "",
      "nib": "",
      "status": "",
      "deletedAt": "",
      "createdAt": "",
      "updatedAt": ""
    }
  ]
}
```

#### Read

- Endpoint: `GET /api/wedding-organizers/:id`
- Response:

```json
{
  "data": {
    "id": "",
    "name": "",
    "thumbnail": "",
    "phone": "",
    "npwp": "",
    "nib": "",
    "rating": "",
    "status": "",
    "deletedAt": "",
    "createdAt": "",
    "updatedAt": "",
    "woPackages": [
      {
        "id": "",
        "name": "",
        "thumbnail": "",
        "city": ""
      }
    ]
  }
}
```

#### Update

- Endpoint: `PUT /api/wedding-organizers/:id`
- Request Body:

```json
{
  "name": "",
  "thumbnail": "",
  "phone": "",
  "npwp": "",
  "nib": ""
}
```

- Response Body (Success):

```json
{
  "success": true,
  "message": "updated successfully"
}
```

#### Destroy

- Endpoint: `DELETE /api/wedding-organizers/:id`
- Response Body (Success):

```json
{
  "success": true,
  "message": "deleted successfully"
}
```

---

### WO Package API

#### Index

- Endpoint: `GET /api/wo-packages`
- Query Param: {name, about, city, is_popular}
- Response Body (Success):

```json
{
  "data": [
    {
      "id": "",
      "name": "",
      "thumbnail": "",
      "city": "",
      "isPoluler": false,
      "deletedAt": "",
      "createdAt": "",
      "updatedAt": "",
      "weddingPhotos": [
        {
          "id": "",
          "photo": ""
        },
        {
          "id": "",
          "photo": ""
        }
      ]
    }
  ]
}
```

#### Read

- Endpoint: `GET /api/wo-packages/:id`
- Response Body (Success):

```json
{
  "success": true,
  "message": "retrieved successfully",
  "data": {
    "id": "",
    "name": "",
    "thumbnail": "",
    "city": "",
    "isPoluler": false,
    "deletedAt": "",
    "createdAt": "",
    "updatedAt": "",
    "weddingPhotos": [
      {
        "id": "",
        "photo": ""
      },
      {
        "id": "",
        "photo": ""
      }
    ]
  }
}
```

#### Create

- Endpoint: `POST /api/wo-packages`
- Accept: Multipart/FormData
- Request Body:

```json
{
  "name": "",
  "thumbnail": "",
  "city": "",
  "isPoluler": false,
  "deletedAt": "",
  "createdAt": "",
  "updatedAt": "",
  "weddingPhotos": [
    {
      "weddingPackageId": "",
      "photo": ""
    }
  ],
  "weddingBonusPackages": [
    {
      "weddingPackageId": "",
      "bonusPackageId": ""
    }
  ]
}
```

- Response Body (Success):

```json
{
  "success": true,
  "message": "created successfully"
}
```

#### Update

- Endpoint: `PUT /api/wo-packages/:id`
- Accept: Multipart/FormData
- Request Body:

```json
{
  "name": "",
  "thumbnail": "",
  "city": "",
  "isPoluler": false,
  "deletedAt": "",
  "createdAt": "",
  "updatedAt": "",
  "weddingPhotos": [
    {
      "weddingOrganizerId": "",
      "photo": ""
    }
  ]
}
```

- Response Body (Success):

```json
{
  "success": true,
  "message": "updated successfully"
}
```

#### Destroy

- Endpoint: `DELETE /api/wo-packages/:id`
- Response Body (Success):

```json
{
  "success": true,
  "message": "deleted successfully"
}
```

---

### Bonus Package API

#### Index

- Endpoint: `GET /api/bonus-packages`
- Query Param: {name, description, price}
- Response Body (Success):

```json
{
  "success": true,
  "message": "retrieved successfully",
  "data": [
    {
      "id": "",
      "name": "",
      "description": "",
      "price": 0,
      "thumbnail": "",
      "minQuantity": 0,
      "maxQuantity": 0
    }
  ]
}
```

#### Read

- Endpoint: `GET /api/bonus-packages/:id`
- Response Body (Success):

```json
{
  "success": true,
  "message": "retrieved successfully",
  "data": {
    "id": "",
    "name": "",
    "description": "",
    "price": 0,
    "thumbnail": "",
    "minQuantity": 0,
    "maxQuantity": 0,
    "bonusPackageDetail": {
      "id": "",
      "bonusPackageId": "",
      "weddingPackageId": "",
      "quantity": 0,
      "isAdjustable": false
    }
  }
}
```

#### Create

- Endpoint: `POST /api/bonus-packages`
- Request Body:

```json
{}
```

- Response Body (Success):

#### Update

- Endpoint: `PUT /api/bonus-packages/:id`
- Request Body:

```json
{}
```

- Response Body (Success):

```json
{
  "success": true,
  "message": "updated successfully"
}
```

#### Destroy

- Endpoint: `DELETE /api/bonus-packages/:id`
- Response Body (Success):

```json
{
  "success": true,
  "message": "deleted successfully"
}
```

---

### BookingTransaction

#### Index

- Endpoint: `GET /api/transactions`
- Query Param: {}
- Response Body (Success):

#### Index By Wedding Organizer

- Endpoint: `GET /api/transactions/:woId`
- Query Param: {}
- Response Body (Success):

```json
{
  "data": [
    {
      "id": "",
      "transDate": "",
      "weddingDate": "",
      "bookCode": "",
      "isPaid": false,
      "basePrice": 0,
      "proofImage": "",
      "customer": {
        "id": "",
        "name": "",
        "phone": "",
        "email": ""
      },
      "weddingPackage": {
        "id": "",
        "weddingOrganizerId": "",
        "name": "",
        "description": "",
        "city": "",
        "basePrice": 0
      },
      "transactionDetail": {
        "id": "",
        "bonusPackageId": "",
        "quantity": 0,
        "price": 0
      }
    }
  ]
}
```

#### Read

- Endpoint: `GET /api/transactions/:id`
- Response Body (Success):

```json
{
  "data": {
    "id": "",
    "transDate": "",
    "weddingDate": "",
    "bookCode": "",
    "isPaid": "",
    "basePrice": "",
    "customer": {
      "id": "",
      "name": "",
      "phone": "",
      "email": ""
    },
    "weddingPackage": {
      "id": "",
      "weddingOrganizerId": "",
      "name": "",
      "description": "",
      "city": "",
      "basePrice": ""
    },
    "transactionDetail": {
      "id": "",
      "bonusPackageId": "",
      "quantity": "",
      "price": ""
    }
  }
}
```

#### Create

- Endpoint: `POST /api/transactions`
- Request Body:

```json
{
  "customer": {
    "name": "",
    "phone": "",
    "email": "",
    "address": ""
  },
  "weddingPackageId": "",
  "weddingDate": "",
  "coupon": ""
}
```

- Response Body (Success):

```json
{
  "success": true,
  "message": "created successfully"
}
```

#### Update

- Endpoint: `PUT /api/transactions/:bookCode`
- Accept: Multipart/FormData
- Request Body:

```json
{
  "proofImage": ""
}
```

- Response Body (Success):

```json
{
  "success": true,
  "message": "updated successfully"
}
```

#### Destroy

- Endpoint: `DELETE /api/transactions/:id`
- Response Body (Success):

```json
{
  "success": true,
  "message": "deleted successfully"
}
```

## Authentication

## Technologies Used

## Contributing

## License