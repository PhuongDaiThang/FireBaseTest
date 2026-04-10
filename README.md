# 🎬 Movie Ticket App

Ứng dụng đặt vé xem phim trên Android, sử dụng **Firebase** làm backend (không cần server riêng).

---

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Screenshots](#screenshots)
- [Kiến trúc & Công nghệ](#kiến-trúc--công-nghệ)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Database Schema (Firestore)](#database-schema-firestore)
- [Chức năng chi tiết](#chức-năng-chi-tiết)
- [Cài đặt & Chạy project](#cài-đặt--chạy-project)
- [Cấu hình Firebase](#cấu-hình-firebase)
- [Seed Data](#seed-data)
- [Luồng hoạt động](#luồng-hoạt-động)

---

## Tổng quan

Movie Ticket App cho phép người dùng:

- **Đăng nhập / Đăng ký** bằng Email hoặc Google Account
- **Xem danh sách phim** đang chiếu (realtime sync)
- **Xem chi tiết phim** và các suất chiếu
- **Chọn ghế & Đặt vé** (atomic transaction đảm bảo không trùng ghế)
- **Xem lịch sử vé** đã đặt (realtime sync)
- **Nhận thông báo** nhắc lịch chiếu trước 30 phút

---

## Screenshots

| Đăng nhập  | Danh sách phim |    Chi tiết phim    |
| :--------: | :------------: | :-----------------: |
| Dark theme |   Grid 2 cột   | Poster + Suất chiếu |

|   Chọn ghế    |      Vé của tôi       |
| :-----------: | :-------------------: |
| Grid 8x10 ghế | Danh sách vé realtime |

---

## Kiến trúc & Công nghệ

| Thành phần         | Công nghệ                                       |
| ------------------ | ----------------------------------------------- |
| Ngôn ngữ           | Java                                            |
| Platform           | Android (minSdk 24, targetSdk 34)               |
| Authentication     | Firebase Auth (Email/Password + Google Sign-In) |
| Database           | Cloud Firestore                                 |
| Push Notification  | Firebase Cloud Messaging (FCM)                  |
| Local Notification | AlarmManager + BroadcastReceiver                |
| Image Loading      | Glide 4.16                                      |
| UI Components      | Material Design Components                      |
| Architecture       | Activity-based + Singleton FirebaseHelper       |

---

## Cấu trúc thư mục

```
MovieTicketApp/
├── app/
│   ├── build.gradle                          # Dependencies (Firebase, Glide, Material)
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/movieticketapp/movieticket/
│       │   ├── activities/
│       │   │   ├── LoginActivity.java        # Đăng nhập (Email + Google)
│       │   │   ├── RegisterActivity.java     # Đăng ký tài khoản
│       │   │   ├── MainActivity.java         # Danh sách phim (Grid)
│       │   │   ├── MovieDetailActivity.java  # Chi tiết phim + Suất chiếu
│       │   │   ├── SeatSelectionActivity.java# Chọn ghế + Đặt vé
│       │   │   └── MyTicketsActivity.java    # Danh sách vé đã đặt
│       │   ├── adapters/
│       │   │   ├── MovieAdapter.java         # RecyclerView adapter cho phim
│       │   │   ├── ShowtimeAdapter.java      # RecyclerView adapter cho suất chiếu
│       │   │   └── TicketAdapter.java        # RecyclerView adapter cho vé
│       │   ├── firebase/
│       │   │   ├── FirebaseHelper.java       # Singleton quản lý Firebase operations
│       │   │   ├── MyFirebaseMessagingService.java  # FCM push notification
│       │   │   └── ShowtimeReminderReceiver.java    # Local alarm nhắc giờ chiếu
│       │   └── models/
│       │       ├── User.java                 # Model người dùng
│       │       ├── Movie.java                # Model phim
│       │       ├── Theater.java              # Model rạp chiếu
│       │       ├── Showtime.java             # Model suất chiếu
│       │       └── Ticket.java               # Model vé
│       └── res/
│           ├── drawable/                     # Seat states, icons
│           ├── layout/                       # XML layouts cho Activities + Items
│           ├── menu/                         # Menu toolbar
│           └── values/                       # Colors, Strings, Themes
├── build.gradle                              # Project-level Gradle
├── settings.gradle
├── gradle.properties
├── .gitignore
└── README.md
```

---

## Database Schema (Firestore)

### 📁 Collection: `users`

| Field         | Type   | Mô tả                           |
| ------------- | ------ | ------------------------------- |
| `uid`         | string | Firebase Auth UID (document ID) |
| `email`       | string | Email đăng ký                   |
| `displayName` | string | Tên hiển thị                    |
| `photoUrl`    | string | URL ảnh đại diện (Google)       |
| `phone`       | string | Số điện thoại                   |
| `fcmToken`    | string | FCM token cho push notification |
| `createdAt`   | number | Timestamp tạo tài khoản         |

### 📁 Collection: `movies`

| Field         | Type            | Mô tả                           |
| ------------- | --------------- | ------------------------------- |
| `title`       | string          | Tên phim                        |
| `description` | string          | Mô tả nội dung                  |
| `posterUrl`   | string          | URL poster phim                 |
| `genre`       | string          | Thể loại (VD: "Action, Sci-Fi") |
| `duration`    | number          | Thời lượng (phút)               |
| `rating`      | number          | Điểm đánh giá (0-10)            |
| `releaseDate` | string          | Ngày khởi chiếu (yyyy-MM-dd)    |
| `trailerUrl`  | string          | URL trailer                     |
| `cast`        | array\<string\> | Danh sách diễn viên             |

### 📁 Collection: `theaters`

| Field        | Type            | Mô tả                             |
| ------------ | --------------- | --------------------------------- |
| `name`       | string          | Tên rạp                           |
| `address`    | string          | Địa chỉ                           |
| `totalSeats` | number          | Tổng số ghế                       |
| `facilities` | array\<string\> | Tiện ích (IMAX, 4DX, Dolby Atmos) |

### 📁 Collection: `showtimes`

| Field         | Type            | Mô tả                          |
| ------------- | --------------- | ------------------------------ |
| `movieId`     | string          | ID phim (reference)            |
| `theaterId`   | string          | ID rạp (reference)             |
| `movieTitle`  | string          | Tên phim (denormalized)        |
| `theaterName` | string          | Tên rạp (denormalized)         |
| `date`        | string          | Ngày chiếu (yyyy-MM-dd)        |
| `time`        | string          | Giờ chiếu (HH:mm)              |
| `timestamp`   | number          | Epoch millis (sort + reminder) |
| `price`       | number          | Giá vé (VNĐ)                   |
| `totalSeats`  | number          | Tổng ghế trong phòng           |
| `bookedSeats` | array\<string\> | Ghế đã đặt (VD: ["A1","B3"])   |

### 📁 Collection: `tickets`

| Field               | Type            | Mô tả                                       |
| ------------------- | --------------- | ------------------------------------------- |
| `userId`            | string          | UID người đặt                               |
| `movieId`           | string          | ID phim                                     |
| `movieTitle`        | string          | Tên phim                                    |
| `posterUrl`         | string          | URL poster                                  |
| `theaterId`         | string          | ID rạp                                      |
| `theaterName`       | string          | Tên rạp                                     |
| `showtimeId`        | string          | ID suất chiếu                               |
| `date`              | string          | Ngày chiếu                                  |
| `time`              | string          | Giờ chiếu                                   |
| `showtimeTimestamp` | number          | Timestamp suất chiếu                        |
| `seats`             | array\<string\> | Ghế đã chọn                                 |
| `totalPrice`        | number          | Tổng tiền (VNĐ)                             |
| `bookedAt`          | number          | Thời điểm đặt vé                            |
| `status`            | string          | Trạng thái: `active` / `used` / `cancelled` |

---

## Chức năng chi tiết

### 1. Authentication (Đăng nhập / Đăng ký)

- **Email + Password**: Đăng ký tạo tài khoản mới, đăng nhập bằng email/password
- **Google Sign-In**: Đăng nhập 1 chạm bằng tài khoản Google
- Tự động lưu thông tin user lên Firestore collection `users`
- Tự động chuyển đến `MainActivity` nếu đã đăng nhập

### 2. Danh sách phim (Realtime)

- Hiển thị dạng **Grid 2 cột** với poster, tên phim, thể loại, rating
- Sử dụng **Firestore Snapshot Listener** → danh sách tự cập nhật khi admin thêm/sửa phim
- Image loading bằng **Glide** với placeholder

### 3. Chi tiết phim & Suất chiếu

- Hiển thị poster lớn, tiêu đề, thể loại, thời lượng, rating, mô tả
- Danh sách suất chiếu (rạp, ngày, giờ, giá, số ghế trống)
- Chỉ hiển thị suất chiếu **trong tương lai** (`timestamp > now`)
- Sắp xếp theo thời gian

### 4. Chọn ghế & Đặt vé

- **Grid ghế 8 hàng × 10 cột** (A1 → H10 = 80 ghế)
- 3 trạng thái ghế:
  - 🟦 **Trống** (xanh navy) - có thể chọn
  - 🟩 **Đã chọn** (xanh lá) - đang chọn
  - ⬛ **Đã đặt** (xám) - không thể chọn
- Hiển thị realtime: ghế đã chọn + tổng tiền
- **Atomic Booking**: sử dụng `WriteBatch` để:
  1. Tạo document ticket mới
  2. Cập nhật `bookedSeats` trong showtime
  - → Đảm bảo không bị trùng ghế khi 2 người đặt cùng lúc

### 5. Vé của tôi (Realtime)

- Hiển thị tất cả vé của user hiện tại
- Mỗi vé hiện: poster, tên phim, rạp, ngày giờ, ghế, giá, trạng thái
- Trạng thái vé có màu: 🟢 Active / ⚪ Used / 🔴 Cancelled
- Sắp xếp theo thời gian đặt (mới nhất trước)
- **Realtime sync** qua Snapshot Listener

### 6. Push Notification

#### FCM (Firebase Cloud Messaging)

- `MyFirebaseMessagingService` nhận push notification từ Firebase Console hoặc Cloud Functions
- Tự động lưu FCM token vào Firestore khi token thay đổi

#### Local Reminder (AlarmManager)

- Khi đặt vé thành công → tự động schedule alarm **trước 30 phút**
- `ShowtimeReminderReceiver` hiện notification: _"Phim X sẽ chiếu lúc Y tại Z"_
- Sử dụng `setExactAndAllowWhileIdle()` để đảm bảo alarm hoạt động khi Doze mode

---

## Cài đặt & Chạy project

### Yêu cầu

- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 8+
- Android SDK 34
- Tài khoản Firebase

### Bước 1: Clone project

```bash
git clone <repository-url>
cd MovieTicketApp
```

### Bước 2: Cấu hình Firebase (xem phần bên dưới)

### Bước 3: Mở bằng Android Studio

- File → Open → chọn thư mục `MovieTicketApp`
- Đợi Gradle sync xong

### Bước 4: Chạy app

- Kết nối thiết bị Android hoặc tạo Emulator (API 24+)
- Run ▶️

---

## Cấu hình Firebase

### Bước 1: Tạo Firebase Project

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Tạo project mới (VD: "MovieTicketApp")

### Bước 2: Thêm Android App

1. Trong Firebase Console → Add app → Android
2. Package name: `com.movieticketapp.movieticket`
3. Download `google-services.json`
4. Copy file vào thư mục `app/` (cùng cấp với `build.gradle`)

### Bước 3: Bật Authentication

1. Firebase Console → Authentication → Sign-in method
2. Bật **Email/Password**
3. Bật **Google** → copy **Web Client ID**
4. Mở `app/src/main/res/values/strings.xml`
5. Thay `YOUR_WEB_CLIENT_ID_HERE` bằng Web Client ID vừa copy

### Bước 4: Tạo Cloud Firestore

1. Firebase Console → Firestore Database → Create database
2. Chọn **Start in test mode** (cho development)
3. Chọn region gần nhất

### Bước 5: (Tùy chọn) Tạo Firestore Index

Nếu gặp lỗi khi query showtimes hoặc tickets, tạo composite index:

```
Collection: showtimes
Fields: movieId (ASC), timestamp (ASC)

Collection: tickets
Fields: userId (ASC), bookedAt (DESC)
```

### Bước 6: (Tùy chọn) Cấu hình FCM

- Để gửi push notification từ server, sử dụng Firebase Console → Messaging
- Hoặc viết Cloud Functions để tự động gửi notification

---

## Seed Data

App tự động seed dữ liệu mẫu khi chạy lần đầu (nếu collection `movies` rỗng):

### 5 Phim mẫu

| Phim                  | Thể loại          | Thời lượng | Rating |
| --------------------- | ----------------- | ---------- | ------ |
| Avengers: Secret Wars | Action, Sci-Fi    | 165 phút   | 8.5    |
| The Batman 2          | Action, Crime     | 150 phút   | 8.2    |
| Interstellar 2        | Sci-Fi, Drama     | 180 phút   | 9.0    |
| Spider-Man: Beyond    | Action, Adventure | 140 phút   | 8.0    |
| Frozen 3              | Animation, Family | 110 phút   | 7.5    |

### 3 Rạp mẫu

| Rạp                  | Địa chỉ                        | Tiện ích          |
| -------------------- | ------------------------------ | ----------------- |
| CGV Vincom Center    | 72 Lê Thánh Tôn, Q.1, TP.HCM   | IMAX, Dolby Atmos |
| Lotte Cinema Nowzone | 235 Nguyễn Văn Cừ, Q.1, TP.HCM | 4DX               |
| Galaxy Nguyễn Du     | 116 Nguyễn Du, Q.1, TP.HCM     | Standard          |

### Suất chiếu

- Mỗi phim × mỗi rạp × 3 ngày (15-17/04/2026)
- Giá: 85,000 ~ 120,000 VNĐ

---

## Luồng hoạt động

```
┌─────────────┐     ┌──────────────┐     ┌───────────────────┐
│ LoginActivity│────▶│ MainActivity │────▶│ MovieDetailActivity│
│ (Email/Google)│    │ (Grid phim)  │     │ (Detail + Showtime)│
└─────────────┘     └──────────────┘     └───────────────────┘
       │                   │                        │
       ▼                   ▼                        ▼
┌──────────────┐   ┌──────────────┐     ┌───────────────────┐
│RegisterActivity│  │MyTicketsActivity│  │SeatSelectionActivity│
│ (Đăng ký)    │   │ (Vé của tôi) │     │ (Chọn ghế + Đặt)  │
└──────────────┘   └──────────────┘     └───────────────────┘
                                                 │
                                                 ▼
                                        ┌──────────────────┐
                                        │ ShowtimeReminder │
                                        │ (Alarm 30 phút)  │
                                        └──────────────────┘
```

---

## Firestore Security Rules (Production)

Khi deploy production, cập nhật Firestore Rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users: chỉ đọc/ghi profile của chính mình
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    // Movies: ai cũng đọc được, chỉ admin ghi
    match /movies/{movieId} {
      allow read: if request.auth != null;
      allow write: if false; // Chỉ admin qua Console
    }
    // Theaters: tương tự movies
    match /theaters/{theaterId} {
      allow read: if request.auth != null;
      allow write: if false;
    }
    // Showtimes: đọc tự do, update bookedSeats khi đặt vé
    match /showtimes/{showtimeId} {
      allow read: if request.auth != null;
      allow update: if request.auth != null
        && request.resource.data.diff(resource.data).affectedKeys().hasOnly(['bookedSeats']);
    }
    // Tickets: chỉ đọc/tạo vé của chính mình
    match /tickets/{ticketId} {
      allow read: if request.auth != null && resource.data.userId == request.auth.uid;
      allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
      allow update: if request.auth != null && resource.data.userId == request.auth.uid
        && request.resource.data.diff(resource.data).affectedKeys().hasOnly(['status']);
    }
  }
}
```

---

## License

ThangPD © 2026. All rights reserved.
