# Web Frontend Yol Haritası

## Karar: Teknoloji Yığını
- **Framework:** React + Next.js (TypeScript)
- **State/Data fetching:** React Query (TanStack Query) — JWT'li REST API çağrıları ve cache yönetimi için
- **Stil:** Tailwind CSS
- **Form yönetimi:** React Hook Form + Zod (validation)
- **HTTP client:** axios (interceptor ile access/refresh token yönetimi)

## Neden Bu Seçim?
Backend, stateless JWT + OAuth2 callback akışlarına sahip bir Spring Boot REST API. Next.js, hem SSR/SEO ihtiyacı olursa hem de saf SPA olarak kullanılabilecek esnekliği sağlıyor; ekip React/JS'e yakın olduğu için öğrenme maliyeti düşük.

## Aşamalar

### 1. Netleştirme (Planlama) Aşaması — şu an buradayız
- [ ] Hangi sayfalar/ekranlar olacak netleştirilecek (login, register, dashboard, sosyal hesap bağlama, analiz sonucu ekranı, admin paneli)
- [ ] Tasarım/UI referansı (Figma veya benzeri) var mı, olacak mı karar verilecek
- [ ] API sözleşmesi (Swagger `/v3/api-docs`) frontend ekibiyle paylaşılacak

### 2. Proje İskeleti
- [ ] Next.js + TypeScript proje kurulumu
- [ ] Klasör yapısı: `app/`, `components/`, `lib/api/`, `hooks/`, `types/`
- [ ] axios instance + JWT interceptor (access token header, 401'de refresh token akışı)
- [ ] `.env` ile API base URL yönetimi

### 3. Kimlik Doğrulama Akışı
- [ ] Login / Register / Email verify sayfaları
- [ ] JWT saklama stratejisi (httpOnly cookie tercih edilir, XSS riskine karşı localStorage'dan kaçınılmalı)
- [ ] Route guard (giriş yapılmamışsa dashboard'a erişim engeli)

### 4. Sosyal Medya Bağlama Akışı
- [ ] Instagram / TikTok / YouTube "bağla" butonları → backend'den OAuth URL alıp yönlendirme
- [ ] OAuth callback sonrası state yönetimi (bağlantı başarılı/başarısız bildirimi)
- [ ] Bağlı hesapları listeleme, silme, senkronize etme (sync) ekranı

### 5. AI Analiz Ekranı
- [ ] Kullanıcının bağlı hesabı için analiz başlatma
- [ ] Analiz sonucunu (OpenAI çıktısı) okunabilir şekilde gösterme
- [ ] Yüklenme/hata durumları (analiz süresi uzun sürebilir, loading state önemli)

### 6. Admin Paneli (opsiyonel, ikinci öncelik)
- [ ] Kullanıcı listeleme, kullanıcı detay görüntüleme

### 7. Test & Yayına Alma
- [ ] Temel component testleri
- [ ] Vercel veya benzeri bir platforma deploy
- [ ] CORS ayarlarının backend `SecurityConfig` tarafında frontend domain'ine göre güncellenmesi

## Açık Kararlar (netleştirilecek)
- Mobil (React Native) ile kod/komponent paylaşımı yapılacak mı, yoksa web tamamen bağımsız mı geliştirilecek?
- Tasarım sistemi hazır mı, yoksa sıfırdan mı oluşturulacak?
