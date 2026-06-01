# Shop Management System

A Java Full Stack application built using standard Java EE (Servlets, JSP), MySQL, and Bootstrap.

## Prerequisites for VSCode

To run this project in Visual Studio Code, you will need the following installed:

1. **Java Development Kit (JDK):** Version 17 or higher.
2. **Apache Maven:** Installed and added to your system `PATH`.
3. **MySQL Server:** Installed and running locally (Default port 3306).
4. **VSCode Extensions:**
   *   **Extension Pack for Java** (by Microsoft)
   *   **Community Server Connectors** (by Red Hat) - *Crucial for running Apache Tomcat in VSCode.*

## Setup Instructions

### 1. Database Setup
1. Open your MySQL client (e.g., MySQL Workbench or terminal).
2. Create the database:
   ```sql
   CREATE DATABASE shop_management;
   ```
3. Update the database credentials in the project. (Once you create `db.properties` in `src/main/resources`).

### 2. Running in VSCode

There are two primary ways to run this WAR project in VSCode:

#### Method A: Using Community Server Connectors (Recommended)
This approach allows you to deploy the app to a local Tomcat server directly from VSCode.

1. Download **Apache Tomcat** (Version 10.1.x is recommended for Jakarta EE 10 / Servlet 6.0, which this project uses) and extract it to a folder on your computer.
2. In VSCode, go to the **SERVERS** view (usually at the bottom of the Explorer pane) provided by the *Community Server Connectors* extension.
3. Click **Create New Server...**
4. Select **No, use server on disk** -> Choose the extracted Tomcat folder.
5. Once the server is added, right-click it and choose **Add Deployment**.
6. Select the `target/ShopManagementSystem-1.0-SNAPSHOT.war` file. (You must build the project first: `mvn clean package`).
7. Right-click the server and choose **Start Server**.
8. The app will be available at: `http://localhost:8080/ShopManagementSystem-1.0-SNAPSHOT/`

#### Method B: Using Maven Tomcat Plugin (Alternative)
You can run an embedded Tomcat via Maven. To do this, you would need to add a modern Cargo or Gretty plugin to your `pom.xml`, as the standard `tomcat7-maven-plugin` does not support Jakarta EE 10 (Servlet 6.0 namespace).

### 3. Project Structure
- `src/main/java/com/shopmanagement/`: Java source code (Controllers, Models, DAOs).
- `src/main/webapp/`: Web resources (JSP files, CSS, JS, Images).
- `pom.xml`: Maven dependencies.

## Next Steps
- Implement the Servlet Controllers in `src/main/java/com/shopmanagement/controller/`.
- Create the Data Access Objects (DAO) for interacting with the MySQL database.
- Build out the respective JSP views.
