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
    participant S as Service de Stockage

    U->>A: DELETE /fichier/:id (token JWT)
    A->>B: Vérifier la validité du JWT
    alt Token expiré ou invalide
        B-->>A: 401 Non autorisé
        A-->>U: 401 Jeton expiré ou invalide
    else Token valide
        B->>D: Récupérer les métadonnées du fichier
        alt Fichier introuvable ou non appartenant à l'utilisateur
            D-->>B: Fichier non trouvé / accès interdit
            B-->>A: 403 Accès refusé
            A-->>U: 403 Fichier non trouvé ou accès interdit
        else Fichier trouvé et accessible
            D-->>B: Métadonnées récupérées
            B->>S: Supprimer le fichier du stockage
            alt Échec suppression dans le service de stockage
                S-->>B: Erreur
                B-->>A: 503 Échec suppression sur le stockage
                A-->>U: 503 Erreur stockage — suppression impossible
            else Suppression réussie
                S-->>B: OK
                B->>D: Supprimer les métadonnées du fichier
                alt Échec suppression BDD
                    D-->>B: Erreur
                    B-->>A: 500 Erreur métadonnées — fichier supprimé mais référence encore présente
                    A-->>U: 500 Suppression partielle — nettoyage en cours
                    
                    D-->>D: Nettoyage planifié
                else Suppression complète
                    D-->>B: Métadonnées supprimées
                    B-->>A: 200 OK — Fichier supprimé
                    A-->>U: 200 Suppression réussie
                end
            end
        end
    end
````
## Diagramme de séquence — Suppression d’un fichier

Ce diagramme décrit le processus de suppression d’un fichier via **HippoNuage**, en assurant à la fois la suppression dans le service de stockage distant (ex : S3) **et** dans la base de données. Il prend également en compte les cas d’erreurs, y compris les suppressions partielles.

### Acteurs impliqués

- **Utilisateur** : déclenche la suppression d’un fichier.
- **API** : reçoit la requête HTTP.
- **Business Logic** : effectue les vérifications, orchestre la suppression.
- **Base de Données** : stocke les métadonnées des fichiers.
- **Service de Stockage** : héberge physiquement les fichiers (ex : AWS S3).

### Étapes du processus

1. L’utilisateur envoie une requête `DELETE /fichier/:id` avec un token JWT.
2. Le token JWT est validé.
3. La logique métier récupère les métadonnées du fichier en base de données et vérifie que le fichier appartient à l’utilisateur.
4. Le fichier est supprimé dans le service de stockage distant.
5. Si la suppression dans le service de stockage échoue, la procédure s’arrête et l’utilisateur est notifié.
6. Si la suppression dans le stockage réussit, les métadonnées sont supprimées en base.
7. Si cette étape échoue, l’utilisateur est informé d’une suppression **partielle**, et un **nettoyage est planifié** automatiquement.
8. En cas de réussite complète, une réponse `200 OK` est retournée.

### Gestion des erreurs

| Code | Signification |
|------|---------------|
| 401  | Jeton JWT invalide ou expiré |
| 403  | Fichier inexistant ou non accessible à l’utilisateur |
| 500  | Échec de suppression en base après succès sur le stockage |
| 503  | Échec de suppression dans le service de stockage |

### Comportement attendu

- **Succès total** : fichier supprimé du stockage **et** des métadonnées.
- **Échec partiel** : si les métadonnées ne peuvent pas être supprimées après la suppression dans le stockage, l’utilisateur est informé, et le nettoyage est pris en charge automatiquement en interne.