# 💬 Application de Chat en Java (Client / Serveur)

## 📌 Description

Cette application est un système de messagerie instantanée développé en Java, basé sur une architecture client-serveur utilisant les sockets.

Elle permet à plusieurs utilisateurs de communiquer en temps réel, en privé ou en groupe, avec persistance des données via une base SQLite.

---

## 🚀 Fonctionnalités

### 🔐 Authentification
- Inscription (REGISTER)
- Connexion (LOGIN)
- Gestion des utilisateurs

### 👥 Gestion des amis
- Envoi de demandes d’amis
- Acceptation / refus
- Liste d’amis
- Statut en ligne / hors ligne

### 💬 Messagerie
- Messages privés entre utilisateurs
- Messages de groupe
- Réception en temps réel
- Historique des messages

### 👨‍👩‍👧 Groupes
- Création de groupes
- Ajout de membres
- Discussion en groupe

### 🔔 Notifications
- Notifications pour :
  - nouvelles demandes d’amis
  - nouveaux messages
  - acceptation de demande

---

## 🛠️ Technologies utilisées

- **Java**
  - Sockets (communication réseau)
  - Threads (gestion multi-clients)
- **Swing**
  - Interface graphique
- **SQLite**
  - Base de données embarquée
- **JDBC**
  - Accès aux données

---
## ⚙️ Lancer l'application

### 📦 Compilation du projet

Sur Linux / Mac  
commande : javac -cp "lib/sqlite-jdbc-3.51.3.0.jar" -d out $(find src -name "*.java")

Sur Windows  
commande : javac -cp "lib/sqlite-jdbc-3.51.3.0.jar" -d out src\**\*.java

---

### 🚀 Lancer le serveur

Sur Linux / Mac  
commande : java -cp "out:lib/sqlite-jdbc-3.51.3.0.jar" server.Server

Sur Windows  
commande : java -cp "out;lib/sqlite-jdbc-3.51.3.0.jar" server.Server

---

### 💬 Lancer le client

Sur Linux / Mac  
commande : java -cp "out:lib/sqlite-jdbc-3.51.3.0.jar" client.Main

Sur Windows  
commande : java -cp "out;lib/sqlite-jdbc-3.51.3.0.jar" client.Main

---

👉 Pour tester l'application, lancer d'abord le serveur, puis un ou plusieurs clients dans des terminaux différents.
