#Ticket platform databse

##ticket
- id PK BIGINT UNIQUE AI NOTNULL
- title VARCHAR(255) NOTNULL
- text TEXT NOTNULL
- user_id FK BINGINT
- category_id FK BINGINT

##note
- id PK BIGINT UNIQUE AI NOTNULL
- text TEXT NOTNULL
- date DATETIME NOTNULL
- user_id FK BINGINT
- ticket_id FK BIGINT

##category
- id PK BIGINT UNIQUE AI NOTNULL
- name CHAR(25) NOTNULL UNIQUE

##role
- id PK BIGINT UNIQUE AI NOTNULL
- name CHAR(10) UNIQUE NOTNULL

##user
- id PK BIGINT UNIQUE AI NOTNULL
- username VARCHAR(40) UNIQUE NOTNULL
- password VARCHRA(225) NOTNULL
- role_id FK BINGINT NOTNULL

##user_role
user_id FK BIGINT UNIQUE AI NOTNULL 
role_id FK BIGINT UNIQUE AI NOTNULL 