# 🎮 Bài Tập Lớn OOP - Bomberman Game

## 📝 Mô tả dự án

Dự án là một trò chơi **Bomberman** được phát triển bằng ngôn ngữ lập trình Java, áp dụng các nguyên lý của **Lập trình Hướng đối tượng (OOP)** như kế thừa, đóng gói, trừu tượng và đa hình.

Trò chơi gồm hai nhóm đối tượng chính:

- **Đối tượng động**: `Bomber`, `Enemy`, `Bomb`
- **Đối tượng tĩnh**: `Grass`, `Wall`, `Brick`, `Portal`, `Item`

---

## 🧱 Thiết kế đối tượng

### 👨‍🚀 Bomber
- Nhân vật chính do người chơi điều khiển.
- Di chuyển 4 hướng: trái, phải, lên, xuống.
- Có thể đặt bomb và sử dụng item.

### 👾 Enemy
- Kẻ địch cần bị tiêu diệt để qua màn.
- Di chuyển ngẫu nhiên hoặc đuổi theo Bomber (tùy loại).

#### Các loại Enemy:
- `Balloom`: Di chuyển ngẫu nhiên với tốc độ cố định.
- `Oneal`: Di chuyển "thông minh", biết đuổi theo Bomber với tốc độ thay đổi.
- `BomberEnemy`: Di chuyển nhanh, biết đuổi Bomber, có thể đặt bomb.

### 💣 Bomb
- Được đặt lên `Grass` và phát nổ sau 2 giây.
- Khi nổ tạo ra các `Flame` theo 4 hướng.
- Kích nổ Bomb khác nếu nằm trong phạm vi.

### 🔥 Flame
- Tạo ra khi bomb phát nổ.
- Tiêu diệt `Enemy`, `Bomber` và phá hủy `Brick`.

### 🌿 Grass
- Mặt đất mà các đối tượng có thể di chuyển qua.
- Cho phép đặt bomb.

### 🧱 Wall
- Tường cố định, không thể phá hủy, không thể đi qua.

### 🧱 Brick
- Có thể bị phá hủy bằng bomb.
- Giấu `Item` hoặc `Portal`.

### 🚪 Portal
- Giúp qua màn chơi mới khi đã tiêu diệt hết Enemy.
- Giấu sau một `Brick`.

### 🎁 Item
- Được giấu sau `Brick`, xuất hiện sau khi `Brick` bị phá.
- Có 3 loại:
  - `SpeedItem`: Tăng tốc độ di chuyển của Bomber.
  - `FlameItem`: Tăng độ dài của Flame.
  - `BombItem`: Tăng số lượng bomb có thể đặt cùng lúc.

---

## 🎮 Gameplay

- Người chơi điều khiển Bomber để đặt bomb tiêu diệt Enemy.
- Khi tất cả Enemy bị tiêu diệt, tìm `Portal` để qua màn.
- Bomber sẽ **thua** nếu va chạm với Enemy hoặc bị bomb nổ trúng.

---

## 💥 Cơ chế nổ

- Bomb phát nổ sau 2 giây, tạo ra các Flame theo 4 hướng (trái, phải, trên, dưới).
- Flame mặc định có độ dài 1 ô, tăng nếu có `FlameItem`.
- Nếu gặp vật cản (`Wall`, `Brick`), Flame dừng lại tại đó.
- Bomb khác nằm trong phạm vi sẽ nổ dây chuyền.

---

## ✅ Các chức năng đã hoàn thành

### 🔹 Gói bắt buộc
1. Thiết kế cây thừa kế cho các đối tượng (`https://drive.google.com/file/d/1-2UA5QvIwgckjBgNTfZtvmUV-srPP1-w/view?usp=sharing`)
2. Xây dựng bản đồ màn chơi từ tệp cấu hình
3. Di chuyển Bomber theo điều khiển từ người chơi
4. Di chuyển Enemy tự động
5. Xử lý va chạm giữa các đối tượng
6. Xử lý bomb nổ và flame
7. Xử lý khi Bomber sử dụng item hoặc vào Portal

### 🔸 Gói tùy chọn
1. Nâng cấp thuật toán tìm đường cho Enemy
2. Cài đặt thêm Enemy:
   - `BomberEnemy`: Di chuyển nhanh, biết đuổi Bomber, có thể đặt bomb
3. Thêm hiệu ứng âm thanh (music & sound effects)
4. Thêm menu: pause, game over
5. Tạo nhiều level với map tuỳ chỉnh (ít nhất 3 level)

---
