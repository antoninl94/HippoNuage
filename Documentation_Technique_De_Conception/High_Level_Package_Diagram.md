This is the way our app architecture has been thought

```mermaid
graph TB
    A[**Front-end**
        -Angular SPA 
        -dockerized
        -Vercel Deployment
        ]
    
    B[**Back-end For Front-end Layer**
     ]
    
    C[**User Auth API**
        -Spring boot
        -Dockerized
        -Railway hosting
    ]
    D[**File UPLOAD API**
        -Spring boot
        -Dockerized
        -Railway hosting
    ]
    E[**File Access API**
        -Spring boot
        -Dockerized
        -Railway hosting
    ]
    F[**Persistence Layer**
        -PostGreSQL
        -Hibernate
        -Dockerized]
    G[**Docker Volume**]

    H[**Storage Server**
        -AWS S3]

    A-->B 
    B-->C
    B-->D
    B-->E

    C-->F
    D-->F
    E-->F

    F-->G 
    G-->H