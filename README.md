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

**Standard Response**

```json
{
  "success": "boolean",
  "message": "String",
  "data": {},
  "paging": {},
  "errorMessage": "String"
}
```

### Authentication

#### Login
- **API Endpoint:** `/api/v1/login`
- **Method:** POST
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "yourpassword"
  }
  ```
- **Response Body:**
    - **Success:**
        - **Status Code:** 200 OK
        - **Response Body:**
      ```json
      {
        "success": true,
        "message": "OK",
        "data": {
          "token": "String Token",
          "userId": "String ID"
        }
      }
      ```
    - **Failed:**
        - **Status Code:** 401 Unauthorized
        - **Response Body:**
      ```json
      {
        "success": false,
        "message": "Unauthorized",
        "errorMessage": "String Error"
      }
      ```



## Authentication

## Technologies Used

## Contributing

## License