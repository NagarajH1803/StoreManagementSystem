# Shop Management System

A Java Full Stack application built using standard Java EE (Servlets, JSP), MySQL, and Bootstrap.

## Prerequisites for Local Development

1. **Java Development Kit (JDK):** Version 17 or higher.
2. **Apache Maven:** Installed and added to your system `PATH`.
3. **MySQL Server:** Installed and running locally (Default port 3306).
4. **Docker** (optional): For containerized deployment.

### VSCode Extensions (if using VSCode)
*   **Extension Pack for Java** (by Microsoft)
*   **Community Server Connectors** (by Red Hat) — for running Apache Tomcat in VSCode.

---

## Setup Instructions

### 1. Clone & Configure Database

```bash
git clone https://github.com/NagarajH1803/StoreManagementSystem.git
cd StoreManagementSystem
```

Create the MySQL database:
```sql
CREATE DATABASE shop_management;
```

Run the schema file:
```bash
mysql -u root -p shop_management < src/main/resources/schema.sql
```

### 2. Configure Database Credentials

Copy the template and fill in your credentials:
```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

Edit `src/main/resources/db.properties`:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/shop_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=YOUR_PASSWORD_HERE
```

> **Note:** `db.properties` is gitignored — your credentials will never be committed.

### 3. Run Locally

#### Option A: Using Tomcat in VSCode (Recommended for Development)
1. Download **Apache Tomcat 10.1.x** and extract it.
2. In VSCode, go to the **SERVERS** view (Community Server Connectors).
3. Click **Create New Server...** → **No, use server on disk** → Choose Tomcat folder.
4. Build the project: `mvn clean package`
5. Right-click the server → **Add Deployment** → Select `target/ShopManagementSystem-1.0-SNAPSHOT.war`.
6. Start the server → Visit `http://localhost:8080/ShopManagementSystem-1.0-SNAPSHOT/`

#### Option B: Using Docker
```bash
docker build -t shop-management .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/shop_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="your_password" \
  shop-management
```
Visit `http://localhost:8080/`

---

## 🚀 Deploy to Render (Free Hosting)

### Prerequisites
- A [Render](https://render.com) account (free)
- A free MySQL database from [Aiven](https://aiven.io), [PlanetScale](https://planetscale.com), or similar

### Step-by-Step

1. **Get a Free MySQL Database**
   - Sign up at [Aiven](https://aiven.io) (free tier: 1 database)
   - Create a MySQL service and note the connection details:
     - Host, Port, Database name, Username, Password
   - Build your JDBC URL: `jdbc:mysql://HOST:PORT/DATABASE?useSSL=true&serverTimezone=UTC`

2. **Import Your Schema**
   - Connect to the remote MySQL and run `src/main/resources/schema.sql`

3. **Deploy on Render**
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click **New** → **Web Service**
   - Connect your GitHub repo: `NagarajH1803/StoreManagementSystem`
   - Render will auto-detect the `Dockerfile`
   - Set **Instance Type** to **Free**

4. **Set Environment Variables** (in Render Dashboard → Environment)

   | Variable       | Value                                                              |
   |----------------|--------------------------------------------------------------------|
   | `DB_URL`       | `jdbc:mysql://your-host:3306/your-db?useSSL=true&serverTimezone=UTC` |
   | `DB_USERNAME`  | Your remote MySQL username                                         |
   | `DB_PASSWORD`  | Your remote MySQL password                                         |

5. **Deploy!**
   - Click **Create Web Service** — Render will build the Docker image and deploy
   - Your app will be live at `https://your-app-name.onrender.com`

> **⚠️ Free Tier Note:** The app sleeps after 15 minutes of inactivity. The first request after sleeping takes ~30-60 seconds.

---

## Environment Variables

The app reads configuration from environment variables first, falling back to `db.properties` for local development.

| Variable      | Description                          | Required |
|---------------|--------------------------------------|----------|
| `DB_URL`      | JDBC connection URL                  | Yes      |
| `DB_USERNAME` | Database username                    | Yes      |
| `DB_PASSWORD` | Database password                    | Yes      |
| `DB_DRIVER`   | JDBC driver class (default: MySQL)   | No       |

---

## Project Structure

```
├── Dockerfile                          # Multi-stage Docker build
├── render.yaml                         # Render deployment config
├── pom.xml                             # Maven dependencies
├── src/main/java/com/shopmanagement/
│   ├── controller/                     # Servlet controllers
│   ├── dao/                            # Data access interfaces
│   ├── daoimpl/                        # DAO implementations
│   ├── filter/                         # Auth & CSRF filters
│   ├── model/                          # Data models
│   ├── service/                        # Service interfaces
│   ├── serviceimpl/                    # Service implementations
│   └── util/                           # DB connection, PDF, QR, etc.
├── src/main/webapp/
│   ├── WEB-INF/views/                  # JSP views (manager/shopkeeper)
│   └── assets/                         # CSS, JS, images
└── src/main/resources/
    ├── db.properties.example           # DB config template
    └── schema.sql                      # Database schema
```

## Tech Stack
- **Backend:** Java 17, Jakarta Servlet 6.0, JSP/JSTL
- **Database:** MySQL 8.x with HikariCP connection pool
- **Frontend:** Bootstrap, custom CSS/JS
- **Libraries:** iText PDF, ZXing QR codes, Gson, jBCrypt
- **Deployment:** Docker + Tomcat 10.1 on Render
