# PAUSE — Micro‑pausas y repasos con mascota (Android + Spring Boot + Postgres)

PAUSE ayuda a crear el hábito de **tomar micro‑pausas** y realizar **repasos rápidos**. Cada acción suma **score/estado** y **desbloqueos** para tu mascota, que puedes **personalizar** con vestuario.

<p align="center">
  <em>Android (Compose) ↔ Spring Boot API ↔ PostgreSQL (Docker)</em>
</p>

---

## 🧭 Tabla de contenido
- [TL;DR (arranque rápido)](#tldr-arranque-rápido)
- [Estructura del repo](#estructura-del-repo)
- [Stack](#stack)
- [Arquitectura (alto nivel)](#arquitectura-alto-nivel)
- [Modelo de dominio](#modelo-de-dominio)
- [Endpoints principales](#endpoints-principales)
- [Backend (Spring Boot)](#backend-spring-boot)
- [Android App (Compose)](#android-app-compose)
- [Despliegue (ideas para producción)](#despliegue-ideas-para-producción)
- [Troubleshooting](#troubleshooting)
- [Roadmap corto](#roadmap-corto)
- [Licencia](#licencia)
- [Créditos](#créditos)

---

## 🚀 TL;DR (arranque rápido)

```bash
# 1) Base de datos (Docker)
docker compose up -d

# 2) Backend (Spring Boot)
cd backend
./gradlew bootRun   # (o: ./mvnw spring-boot:run)

# 3) Android (App)
# Abre el proyecto en Android Studio, módulo "app"
# BASE_URL dev (emulador): http://10.0.2.2:8080/api/pause
# Ejecuta en el emulador o dispositivo real (misma red que el backend)
```

---

## 🗂️ Estructura del repo

```
.
├─ backend/                      # Spring Boot (Kotlin/Java)
│  ├─ src/main/kotlin/com/pause/backend/...
│  ├─ src/main/resources/application.properties
│  └─ build.gradle.kts | pom.xml
├─ app/                          # Android (Kotlin + Compose)
│  ├─ src/main/java/com/pause/frontend/...
│  ├─ src/main/res/              # fondos, sprites, íconos
│  └─ build.gradle.kts
├─ docker-compose.yml            # Postgres en Docker
└─ README.md
```

---

## 🧰 Stack

- **Android**: Kotlin · Jetpack **Compose** · Navigation · ViewModel + StateFlow · Retrofit · DataStore
- **Backend**: Spring Boot · Web · Data JPA/Hibernate · (Kotlin/Java) · Manejo de errores
- **DB**: PostgreSQL (Docker) · JSONB para `equippedItems`/`unlockedItems`

---

## 🏗️ Arquitectura (alto nivel)

**Frontend (MVVM)**  
`UI(Compose) → ViewModel → Repository → Retrofit(API)`  
`↘ DataStore (userId/petId, flags)`

**Backend (Capas)**  
`Controllers (REST) → Services (reglas, @Transactional) → Repositories (JPA)` → **Postgres**

---

## 🧱 Modelo de dominio

- **User** (id, email, userName) — 1:1 con **Pet**  
- **Pet** (id, userId, petName, stateLevel, score, `unlockedItems[]`, `equippedItems{slot→item?}`, lastUpdatedAt)  
- **Pause** (id, userId, durationMinutes, timestamp, clientEventId?)  
- **Review** (id, userId, questionId, question, userAnswer, correct, topic, timestamp)

**Slots soportados v1**  
- `head` → `hat_red`, `hat_blue`, `crown`  
- `eyes` → `sunglasses`

---

## 🔌 Endpoints principales

| Método | Path                                   | Descripción |
|:--|:--|:--|
| GET | `/api/pause/summary?userId={id}` | Estado consolidado (nivel, score, `unlockedItems`, `equippedItems`, próximos desbloqueos, `userName`, `petName`) |
| POST | `/api/pause/users` | Crear usuario `{ email, userName? }` |
| PATCH | `/api/pause/users/{id}/name` | Cambiar nombre de usuario |
| DELETE | `/api/pause/users/{id}` | Eliminar usuario (y datos asociados) |
| POST | `/api/pause/pets` | Crear mascota `{ userId, petName }` |
| PATCH | `/api/pause/pets/{id}/name` | Cambiar nombre de mascota |
| PATCH/PUT | `/api/pause/pets/{id}/equip` | Equipar/retirar `{ "equippedItems": { "head": "...", "eyes": "..." } }` |
| POST | `/api/pause/pauses` | Registrar pausa `{ userId, durationMinutes, timestamp, clientEventId? }` |
| POST | `/api/pause/reviews` | Registrar repaso `{ userId, questionId, question, userAnswer, correct, topic, timestamp }` |

**Códigos de error**: `400` (validación), `404` (no existe), `409` (duplicado por `clientEventId`, si se usa), `500` (genérico controlado).

---

## 🖥️ Backend (Spring Boot)

### Requisitos
- JDK **21** 
- Docker/Docker Desktop

### Configuración DB (Docker)
`docker-compose.yml`:

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: pause
      POSTGRES_USER: pause
      POSTGRES_PASSWORD: pause
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
volumes:
  pgdata:
```

Levantar:

```bash
docker compose up -d
```

### Config (dev)
`backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pause
spring.datasource.username=pause
spring.datasource.password=pause

# Dev: útil mientras iteras; en prod usa Flyway/Liquibase
spring.jpa.hibernate.ddl-auto=update

spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
server.port=8080
```

### Ejecutar el backend

```bash
cd backend
./gradlew bootRun
# o con Maven:
# ./mvnw spring-boot:run
```

---

## 🤖 Android App (Compose)

### Requisitos
- Android Studio **Koala+**  
- minSdk **33**, targetSdk **35**

### Configuración de red
- Emulador Android → host: `10.0.2.2`  
  `BASE_URL = http://10.0.2.2:8080/api/pause`
- Dispositivo físico (misma red del backend) → IP de tu PC:  
  `http://<IP_DE_TU_PC>:8080/api/pause`
- `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
- Si usas **HTTP** en dev, mantente en emulador o configura `networkSecurityConfig`.

### Dónde poner el BASE_URL
En tu **ApiClient** o en `BuildConfig` por sabor `debug`/`release`.  
Ejemplo (dev fijo):

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/api/pause/"
```

### Cómo correr
1) Abre el proyecto en Android Studio.  
2) Selecciona módulo `app`.  
3) Ejecuta en emulador o dispositivo.

### Flujo en la app
1. **Login**: email válido `*@*.*` → crea `User` y `Pet("nano", estado 3)`  
2. **Home**: “Bienvenido/a {userName}” / “{petName} – Nivel {stateLevel}”  
3. **Pausa**: diálogo “glass”, countdown + tip, “Terminar ahora / Cancelar” → `POST /pauses`  
4. **Repaso**: pregunta aleatoria → `POST /reviews`  
5. **Vestuario**: equipar/retirar por slot (chips + “Ninguno”), guardar  
6. **Settings**: renombrar usuario/mascota, **Salir**, **Eliminar perfil**

> Al volver de Vestuario/Settings, Home se refresca (flag con `SavedStateHandle`).

### Recursos gráficos
- Fondos por pantalla: `res/drawable/bg_home.png`, `bg_pause.png`, `bg_review.png`, `bg_wardrobe.png`, `bg_settings.png`, `bg_login.png`.
- Sprites mascota: base por `stateLevel`, accesorios por slot (PNG con transparencia).
- **Icono app**: Android Studio → **Image Asset** → Adaptive icon (`ic_launcher`).
