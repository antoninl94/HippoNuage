```mermaid
---
config:
  theme: redux-dark-color
  look: neo
---
sequenceDiagram
    participant U as Utilisateur
    participant A as API
    participant B as Business Logic
    participant D as Base de Données
    participant S as Service de Validation du Stockage

    U->>A: POST /uploadFichier (fichier, token JWT)
    A->>B: Vérifier la validité du JWT
    alt Token expiré ou invalide
        B-->>A: 401 Non autorisé
        A-->>U: 401 Jeton expiré ou invalide, veuillez vous reconnecter
    else Token valide
        B->>B: Vérifier la taille du fichier
        alt Taille excessive
            B-->>A: 413 Payload trop volumineux
            A-->>U: 413 Fichier trop lourd
        else Taille correcte
            B->>B: Vérifier le MIME type
            alt MIME non supporté
                B-->>A: 415 Type MIME non autorisé
                A-->>U: 415 Type de fichier non pris en charge
            else MIME valide
                B->>D: Enregistrer les métadonnées du fichier
                alt Échec insertion métadonnées
                    D-->>B: Insertion échouée
                    B-->>A: Erreur serveur inattendue
                    alt Types d'erreur possible
                        A-->>U: 500 Erreur Serveur Interne
                    else
                        A-->>U: 503 Service Indisponible
                    end
                else Insertion réussie
                    D-->>B: Métadonnées enregistrées
                    B->>S: Lancer validation du stockage
                    alt Service indisponible ou échec upload
                        S-->>B: Échec
                        B->>D: Supprimer les métadonnées du fichier
                        D-->>B: Suppression OK
                        B-->>A: 503 Service de stockage indisponible
                        A-->>U: 503 Erreur de stockage – fichier non sauvegardé
                    else Validation OK
                        S-->>B: Stockage validé
                        B-->>A: 200 OK (URL fichier, ID, timestamp, métadonnées)
                        A-->>U: 200 Upload réussi (avec infos)
                    end
                end
            end
        end
    end
````

##  Upload de fichier — Diagramme de Séquence

Ce diagramme décrit le **processus complet d'upload d'un fichier** dans notre application cloud, en assurant la sécurité, la validation, et la cohérence des données entre les différents services :

1. **🔒 Authentification JWT**  
   L'utilisateur envoie une requête `POST /uploadFichier` contenant le fichier et son token JWT.  
   L’API transmet cette requête à la couche métier pour valider le token.  
   - Si le token est invalide ou expiré → réponse `401 Unauthorized`.

2. **Vérification de la taille du fichier**  
   La couche métier vérifie que la taille du fichier respecte les limites définies.  
   - Si le fichier est trop lourd → réponse `413 Payload Too Large`.

3. **Vérification du type MIME**  
   Le type MIME du fichier est contrôlé pour empêcher l’upload de fichiers non autorisés (exécutables, malwares…).  
   - Si le type MIME n’est pas autorisé → réponse `415 Unsupported Media Type`.

4. **Enregistrement des métadonnées**  
   Si les vérifications sont valides, les métadonnées (nom, format, taille, utilisateur, etc.) sont enregistrées dans la base de données.  
   - Si l’insertion échoue → réponse `500 Internal Server Error` ou `503 Service Unavailable`.

5. **Validation et envoi au service de stockage**  
   Ensuite, le fichier est transféré vers le service de stockage (ex. AWS S3).  
   - En cas d’échec (erreur réseau, indisponibilité...), les **métadonnées sont supprimées** de la base pour garantir la cohérence.  
   - Réponse `503 Storage Error` envoyée à l’utilisateur.

6. **Réussite de l’upload**  
   Si tout est validé, la réponse `200 OK` contient :
   - L’URL publique du fichier
   - L’ID du fichier
   - Le timestamp
   - Les métadonnées associées

---

###  Objectif

Cette séquence permet :
- Une **protection robuste** contre les fichiers malicieux (validation MIME)
- Une **cohérence des données** avec rollback en cas d'échec
- Une **expérience utilisateur claire** avec des retours précis