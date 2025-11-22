# FLIXGO - Movie Rental and Review Platform
A web-based movie rental and management system built with Java Servlets and file handling (.txt files for data persistence).

## 🎬 Features

### User Features
- User registration and authentication
- Browse and search movie catalog
- Watch movies and trailers
- View watched history (implemented using **Stack** data structure)
- Write and manage movie reviews
- Payment method management
- Purchase premium tickets

### Admin Features
- Admin dashboard with statistics
- User management (add/delete users)
- Admin management (add/edit/delete admins)
- Movie management (add/edit/delete movies)
- View ticket sales income

## 🛠️ Technologies Used

- **Backend:** Java Servlets
- **Frontend:** HTML, CSS, JavaScript
- **Data Storage:** File Handling (.txt files)
- **Sorting Algorithm:** Bubble Sort (for movie sorting by rating)
- **Data Structure:** Stack (for watched movie history)


## ⚙️ Configuration - Important File Paths

**Before running the project, you MUST update the following file paths according to your system:**

### 1. FileUtil.java - BASE_PATH
```java
// Location: src/main/java/com/flixgo/util/FileUtil.java
public static final String BASE_PATH = "C:/your-path/FLIXGO/data/";
```
Change this to your local data directory path where .txt files will be stored.

### 2. MoviePosterController.java - POSTER_BASE_PATH
```java
// Location: src/main/java/com/flixgo/controller/MoviePosterController.java
private static final String POSTER_BASE_PATH = "C:/your-path/FLIXGO/posters/";
```
Change this to your local directory path where movie poster images are stored.

### 3. VideoServlet.java - MOVIES_DIR
```java
// Location: src/main/java/com/flixgo/servlet/VideoServlet.java
private static final String MOVIES_DIR = "C:/your-path/FLIXGO/videos/";
```
Change this to your local directory path where movie video files are stored.

## 📊 Data Structures & Algorithms

### Bubble Sort
Used for sorting movies by rating in descending order.
```java
// Movies are sorted by rating when user clicks "Sort by Rating"
// Implementation in MovieServlet or MovieService
```

### Stack (Watched History)
Used to maintain user's watched movie history with LIFO (Last In, First Out) behavior.
```java
// Most recently watched movie appears first
// Implementation in WatchedHistoryService
```

## 🔐 Default Admin Credentials

```
Username: admin
Password: admin123
```

## 📱 Pages Overview

| Page | Description |
|------|-------------|
| `index.html` | Landing page with video background |
| `login.html` | User login |
| `register.html` | User registration |
| `movie.html` | Movie catalog with search and sort |
| `UserDashboard.html` | User account management |
| `WatchedHistory.html` | User's watched movies (Stack) |
| `ReviewPage.html` | Write/edit movie reviews |
| `PaymentMethods.html` | Manage payment cards |
| `Ticketpage.html` | Purchase premium tickets |
| `adminLogin.html` | Admin login |
| `adminDashboard.html` | Admin control panel |
| `movieManagement.html` | Admin movie CRUD |
| `AllReviews.html` | Public reviews page |

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

##  Acknowledgments

- Built as a university/college project
- Uses file handling for data persistence instead of a database
- Demonstrates practical use of data structures (Stack) and algorithms (Bubble Sort)
