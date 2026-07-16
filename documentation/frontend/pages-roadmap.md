# Sayfa Yol Haritası (Backend'e Uygun)

Bu doküman, mevcut backend uçlarına birebir karşılık gelen frontend sayfalarını
ve geliştirme sırasını tanımlar. Genel teknoloji kararları için
`roadmap.md`'ye bakın.

## Sayfa → Backend Eşlemesi

| # | Sayfa | Route | Kullandığı Backend Uçları | Amaç | Durum |
|---|-------|-------|---------------------------|------|-------|
| 1 | Giriş | `/login` | `POST /api/auth/login` | Kullanıcı girişi, JWT alma | ✅ Yapıldı |
| 2 | Kayıt | `/register` | `POST /api/auth/register` | Yeni kullanıcı kaydı | ✅ Yapıldı |
| 3 | E-posta Doğrulama | `/verify-email?token=` | `GET /api/auth/verify-email` | Kayıt sonrası e-posta doğrulama | ✅ Yapıldı |
| 4 | Dashboard / Ana | `/` | (özet için 5. ve 7.'yi kullanır) | Genel bakış, hızlı erişim | 🟡 Basit shell var |
| 5 | Sosyal Hesaplar | `/accounts` | `GET/POST/DELETE /api/social-media`, `POST /api/social-media/{id}/sync`, `POST /api/social-media/sync` | Bağlı hesapları listele, ekle, sil, senkronize et | ✅ Yapıldı |
| 6 | Hesap Bağla (OAuth) | `/accounts` içinde panel | `GET /api/instagram/oauth/url`, `GET /api/youtube/oauth/url`, `GET /api/tiktok/oauth/url` | Platform yetkilendirme akışını başlat | ✅ Yapıldı |
| 7 | AI Analiz | `/analyze` | `POST /api/openai/analyze` | Bağlı hesap için AI içgörüsü üret | ✅ Yapıldı |
| 8 | Profil / Ayarlar | `/profile` | `GET /api/auth/profile/{username}`, `PUT /api/auth/change-password` | Profil görüntüleme, şifre değiştirme | ✅ Yapıldı |
| 9 | Admin Paneli | `/admin` | `GET /api/admin/users`, `GET /api/admin/inquireUser/{userId}` | (ADMIN) kullanıcı listeleme/detay | ✅ Yapıldı |

## Geliştirme Sırası

1. **Auth tamamlama** ✅ — Kayıt (`/register`) + e-posta doğrulama (`/verify-email`).
   Login zaten hazır; bu ikisiyle uçtan uca kullanıcı akışı kapanır.
2. **Route guard + layout** ✅ — Giriş yapılmamışsa korumalı sayfaları `/login`'e
   yönlendiren guard (`(protected)` route grubu + `AuthGuard`); ortak `Header`.
3. **Sosyal Hesaplar (`/accounts`)** ✅ — Listeleme + sil + sync (tekil/tümü).
   Manuel ekleme (`POST /api/social-media`) formu da burada.
4. **Hesap Bağla (OAuth)** ✅ — Instagram, YouTube ve TikTok butonları
   `/accounts` panelinde yer alır. Callback, bağlantı sonucunu frontend'e
   yönlendirir ve hesap listesi yenilenir.
5. **AI Analiz (`/analyze`)** ✅ — Prompt gönder, dönen analizi göster
   (uzun sürebileceği için loading state; hesabı backend otomatik seçtiği
   için sadece prompt gönderilir).
6. **Profil / Ayarlar** ✅ — Profil görüntüleme + şifre değiştirme
   (username, JWT subject'inden çözülür).
7. **Admin Paneli** ✅ — Rol bazlı erişim (`AdminGuard`, JWT `authorities`
   claim'inden `ROLE_ADMIN`); kullanıcı listeleme + detay. Admin nav linki
   yalnızca yetkili kullanıcıya görünür.

## OAuth Notları (Backend Davranışı)

Bu akış, backend'in mevcut yapısına göre en dikkat gerektiren kısım:

- **`/url` uçları JSON döndürür, ham string değil.** Örnek:
  `GET /api/instagram/oauth/url` → `{ "authUrl": "https://..." }`.
  Frontend `response.authUrl` alanını okumalı.
  > ✅ `src/lib/api/social.ts` `OAuthUrlResponse { authUrl }` tipine göre
  > düzeltildi.
- **Backend callback'i frontend'e geri yönlendirir.** Başarı veya hata sonrası
  kullanıcı `/accounts?oauth={platform}&status={success|error}` adresine döner;
  frontend sonucu gösterir ve başarılı durumda hesap listesini yeniler.
- **TikTok endpoint'i** `/api/tiktok/oauth/url` olarak diğer sağlayıcılarla aynı
  `{ "authUrl": "..." }` sözleşmesini kullanır.
- **`state` parametresi** backend tarafından süreli ve tek kullanımlık tutulur;
  OAuth başlatılırken kullanıcı giriş yapmış olmalıdır.

## AI Analiz Notu

`POST /api/openai/analyze` yalnızca `{ "prompt": "..." }` alır; analiz edilecek
hesabı **backend `authentication.getName()` ile otomatik seçer**. Yani frontend'de
hesap seçtirmeye gerek yok — sadece geçerli bir JWT ile prompt göndermek yeterli.
Yanıt düz metin (string) olarak döner ve analiz süresi uzun olabilir (loading state
önemli).

## Açık Kararlar

- Kayıt sonrası akış: e-posta doğrulama zorunlu mu, doğrulanmadan login engelli mi?
- Admin paneli bu frontend'e mi dahil, yoksa ayrı bir arayüz mü?
