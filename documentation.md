# Documentation des API de Gestion des Utilisateurs (Lecteurs)

## 1. Récupérer tous les lecteurs

**Méthode :** GET

**URL :** `/readers/all`

**Description :** Récupère tous les utilisateurs avec le rôle de USER et leurs profils correspondants.

**Exemple de requête :**

    http GET | /readers/all

## 2. Récupérer un lecteur par ID

**Méthode :** GET

**URL :** `/readers/uuid/{id}`

**Description :** Récupère un utilisateur avec l'ID donné et son profil correspondant.

**Exemple de requête :**

    - http GET | /readers/uuid/123


## 3. Récupérer un lecteur par nom d'utilisateur ou email

**Méthode :** GET

**URL :** `/readers/usem/{value}`

**Description :** Récupère un utilisateur avec le nom d'utilisateur ou l'email donné et son profil correspondant.

**Exemple de requête :** 

    - http GET | /readers/usem/@username


## 4. Créer un nouveau lecteur

**Méthode :** POST

**URL :** `/readers`

**Description :** Crée un nouvel utilisateur et un profil avec les informations fournies. Envoie un email de bienvenue à l'utilisateur.

**Exemple de requête :**

    - http POST | /readers Content-Type: application/json

    {
        "email": "john.doe@example.com", 
        "username": "johndoe", 
        "password": 
        "newpassword123", 
        "telephone": "1234567890", 
        "gender": "M", 
        "firstname": "John", 
        "lastname": "Doe" 
    }


## 5. Mettre à jour un lecteur

**Méthode :** PUT

**URL :** `/readers/{id}`

**Description :** Met à jour les informations d'un lecteur avec l'ID donné.

**Exemple de requête :**

    - http PUT | /readers/123 Content-Type: application/json`

    { 
        "email": "john.doe@example.com", 
        "username": "johndoe", 
        "password": 
        "newpassword123", 
        "telephone": "1234567890", 
        "gender": "M", 
        "firstname": "John", 
        "lastname": "Doe" 
    }

## 6. Mettre à jour partiellement un lecteur

**Méthode :** PATCH

**URL :** `/readers/{id}`

**Description :** Met à jour partiellement les informations d'un lecteur avec l'ID donné.

**Exemple de requête :**

    - http PATCH | /readers/123 Content-Type: application/json

    { 
        "email": "john.doe@example.com" 
    }


## 7. Supprimer un lecteur

**Méthode :** DELETE

**URL :** `/readers/{id}`

**Description :** Supprime un lecteur avec l'ID donné.

**Exemple de requête :**

    - http DELETE | /readers/123

# API - Gestion des Notifications

## 1. Envoyer une notification à un utilisateur

**Méthode :** POST

**URL :** `/notifications/{id}`

**Description :** Envoie une notification à un utilisateur avec l'ID donné.

**Exemple de requête :**

    http POST | /notifications/123 Content-Type: application/json

    {
        "content": "Notification via API Rest Test"
    }


## 2. Récupérer toutes les notifications d'un utilisateur

**Méthode :** GET

**URL :** `/notifications/{userId}`

**Description :** Récupère toutes les notifications d'un utilisateur avec l'ID donné.

**Exemple de requête :**

    http GET | /notifications/123


# API - Gestion des Livres

## 1. Récupérer tous les livres

**Méthode :** GET

**URL :** `/books/all`

**Description :** Récupère tous les livres de la base de données.

**Exemple de requête :**

    http GET | /books/all


## 2. Récupérer les N meilleurs livres

**Méthode :** GET

**URL :** `/books/top/{n}`

**Description :** Récupère les N meilleurs livres de la base de données.

**Exemple de requête :**

    http GET | /books/top/5


## 3. Récupérer un livre par ID

**Méthode :** GET

**URL :** `/books/{id}`

**Description :** Récupère un livre avec l'ID donné de la base de données.

**Exemple de requête :**

    http GET | /books/123


## 4. Créer un nouveau livre

**Méthode :** POST

**URL :** `/books`

**Description :** Crée un nouveau livre avec les données fournies.

**Exemple de requête :**

    http POST | /books Content-Type: application/json

    {
        "title": "Les histoir de Trafalgar Water D. Law",
        "description": "L'histoire de l'un des pirates les plus importants et stratège de One Piece.",
        "year": 2024,
        "authors": "Eiichiro ODA",
        "thumbnail": "law.jpg",
        "category": "Manga",
        "code": "ADFE54E",
        "copies": 99,
        "limitedDescription": "Histoire de Law"
    }


## 5. Mettre à jour un livre

**Méthode :** PUT

**URL :** `/books/{id}`

**Description :** Met à jour un livre avec l'ID donné en utilisant les données de la requête.

**Exemple de requête :**

    http PUT | /books/123 Content-Type: application/json

    {
        "id": 6,
        "title": "1984",
        "description": "A dystopian novel set in a totalitarian state.",
        "year": 1949,
        "authors": "George Orwell",
        "thumbnail": "1984.jpg",
        "category": "Novel",
        "code": "ORW1949",
        "copies": 15,
        "imageBase64": null,
        "limitedDescription": "A dystopian novel set in a totalitarian state."
    }


## 6. Supprimer un livre

**Méthode :** DELETE

**URL :** `/books/{id}`

**Description :** Supprime un livre avec l'ID donné.

**Exemple de requête :**

    http | DELETE /books/123

# API - Gestion des Emprunts

## 1. Récupérer les emprunts d'un utilisateur

**Méthode :** GET

**URL :** `/loans/{userId}`

**Description :** Récupère les emprunts d'un utilisateur avec l'ID donné.

**Exemple de requête :**

    http GET | /loans/123


## 2. Récupérer un emprunt par ID

**Méthode :** GET

**URL :** `/loans/read/{id}`

**Description :** Récupère un emprunt avec l'ID donné.

**Exemple de requête :**

    http GET | /loans/read/456


## 3. Créer un nouvel emprunt

**Méthode :** POST

**URL :** `/loans`

**Description :** Crée un nouvel emprunt pour un livre avec les données fournies.

**Exemple de requête :**

    http POST | /loans Content-Type: application/json

    { 
        "code": "LP1943", 
        "username": "@johndoe", 
        "due": "2023-04-01",
        "counter": 123
    }


## 4. Retourner un emprunt

**Méthode :** PUT

**URL :** `/loans/{id}/return`

**Description :** Met à jour un emprunt pour indiquer qu'il a été retourné.

**Exemple de requête :**

    http PUT | /loans/456/return Content-Type: application/json

    { 
        "returnDate":  "2023-04-01T10:00:00Z" 
    }


## 5. Supprimer un emprunt

**Méthode :** DELETE

**URL :** `/loans/{id}`

**Description :** Supprime un emprunt avec l'ID donné.

**Exemple de requête :**

    http DELETE | /loans/456

# API - Gestion des Réservations

## 1. Récupérer les réservations d'un utilisateur

**Méthode :** GET

**URL :** `/reservations/{userId}`

**Description :** Récupère les réservations d'un utilisateur avec l'ID donné.

**Exemple de requête :**

    http GET | /reservations/123


## 2. Récupérer une réservation par ID

**Méthode :** GET

**URL :** `/reservations/read/{id}`

**Description :** Récupère une réservation avec l'ID donné.

**Exemple de requête :**

    http GET | /reservations/read/456


## 3. Créer une nouvelle réservation

**Méthode :** POST

**URL :** `/reservations`

**Description :** Crée une nouvelle réservation pour un livre avec les données fournies.

**Exemple de requête :**

    http POST | /reservations Content-Type: application/json

    { 
        "code": "LP1943", 
        "username": "johndoe", 
        "status": false,
        "counter": 12
    }  


## 4. Mettre à jour le statut d'une réservation

**Méthode :** PUT

**URL :** `/reservations/{id}/status`

**Description :** Met à jour le statut d'une réservation et envoie un email de confirmation si le statut est mis à jour avec succès.

**Exemple de requête :**

    http PUT | /reservations/456/status Content-Type: application/json

    { 
        "status": true 
    }


## 5. Supprimer une réservation

**Méthode :** DELETE

**URL :** `/reservations/{id}`

**Description :** Supprime une réservation avec l'ID donné.

**Exemple de requête :**

    http DELETE | /reservations/456
