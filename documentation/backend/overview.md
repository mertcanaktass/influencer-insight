# Backend Genel Bakış

## Projenin Amacı
Influencer Insight, kullanıcıların Instagram/TikTok/YouTube hesaplarını bağlayıp, bu hesaplardan çekilen verileri OpenAI (GPT-4o-mini) ile analiz ederek AI destekli influencer performans içgörüleri sunan bir backend servisidir.

## İş Akışı
1. Kullanıcı kayıt olur, e-posta doğrulaması yapar, JWT ile giriş yapar.
2. Sosyal medya hesabını OAuth2 ile bağlar (Instagram/TikTok/YouTube).
3. Bağlı hesabın verisi senkronize edilir (platform API'sinden çekilir, DB'ye kaydedilir).
4. Senkronize edilen veri, prompt haline getirilip OpenAI'a gönderilir; sonuç kullanıcıya analiz olarak döner.

## Teknoloji Yığını
| Kategori | Detay |
|---|---|
| Dil | Java 21 |
| Framework | Spring Boot 3.5.3 (Web MVC, Data JPA, Security, OAuth2 Client, Mail, Validation) |
| Build | Maven (`mvnw`) |
| Veritabanı | PostgreSQL 14 (Docker Compose) + Hibernate/Spring Data JPA |
| Auth | JWT (jjwt), Spring Security, BCrypt, stateless session |
| API Docs | springdoc-openapi (Swagger UI) |
| Test | JUnit 5, Testcontainers (Postgres) |
| Dış Entegrasyonlar | OpenAI API, TikTok Open API, Instagram/Facebook Graph API, Google/YouTube API |
| E-posta | Gmail SMTP |

## Mimari
Katmanlı mimari: `controller` → `service` → `repository` → `model` (JPA entity), ayrıca `dto`, `mapper`, `security`, `util` katmanları.

Sosyal medya platform entegrasyonları **Strategy + Registry pattern** ile soyutlanmıştır (`social/SocialPlatformProvider`, `social/SocialProviderRegistry`, `social/provider/*`). Yeni bir platform eklemek için yalnızca yeni bir `SocialPlatformProvider` implementasyonu yazmak yeterlidir.

## Kullanılan Design Pattern'ler
- **Strategy** — platform bazlı veri çekme mantığı (`InstagramProvider`, `TiktokProvider`, `YoutubeProvider`)
- **Registry** — çalışma zamanında doğru provider'ın seçilmesi (`SocialProviderRegistry`)
- **Chain of Responsibility** — Servlet filter zinciri (`JwtAuthenticationFilter`, `LoggingFilter`)
- **Centralized Exception Handling** — `@RestControllerAdvice` (`GlobalExceptionHandler`)
- **Factory Method** — Spring `@Bean` üretim metodları (`OpenAIConfig`, `SecurityConfig`)
- **Dependency Injection / IoC** — Lombok `@RequiredArgsConstructor` ile constructor injection
- **Repository** — Spring Data JPA (`JpaRepository` extend eden interface'ler)
- **Mapper** — Entity ↔ DTO dönüşümü için statik yardımcı sınıflar

## API Endpoint'leri

| Endpoint | Metod | İş Mantığı |
|---|---|---|
| `/api/admin/register` | POST | Yeni admin kullanıcı kaydı (sadece ADMIN rolü) |
| `/api/admin/users` | GET | Tüm kayıtlı kullanıcıları listeler |
| `/api/admin/inquireUser/{userId}` | GET | ID'ye göre tek bir kullanıcıyı getirir |
| `/api/auth/register` | POST | Yeni (müşteri) kullanıcı kaydı |
| `/api/auth/login` | POST | Kimlik doğrulama, JWT access/refresh token üretimi |
| `/api/auth/refresh` | POST | Refresh token ile yeni access token üretimi |
| `/api/auth/logout` | POST | Kullanıcı çıkışı, token'ı kara listeye alır |
| `/api/auth/profile/{username}` | GET | Kullanıcının kendi profil bilgisini görüntüler |
| `/api/auth/change-password` | PUT | Kimliği doğrulanmış kullanıcının şifresini değiştirir |
| `/api/auth/verify-email` | GET | Token ile e-posta doğrulama |
| `/api/instagram/oauth/url` | GET | Instagram OAuth yetkilendirme URL'i üretir |
| `/api/instagram/oauth/callback` | GET | Instagram OAuth callback'ini işler ve sonucu frontend hesaplar sayfasına yönlendirir |
| `/api/openai/chat` | POST | Verilen prompt'u OpenAI'a gönderip ham yanıt döner |
| `/api/openai/analyze` | POST | Kullanıcının bağlı sosyal medya hesabını OpenAI ile analiz eder |
| `/api/social-media` | POST | Kullanıcıya sosyal medya hesabı ekler/bağlar |
| `/api/social-media` | GET | Kullanıcının bağlı sosyal medya hesaplarını listeler |
| `/api/social-media/{id}` | DELETE | Bağlı sosyal medya hesabını kaldırır |
| `/api/social-media/{id}/sync` | POST | Belirli bir sosyal medya hesabının verisini senkronize eder |
| `/api/social-media/sync` | POST | Kullanıcının tüm bağlı hesaplarının verisini senkronize eder |
| `/api/tiktok/oauth/url` | GET | TikTok OAuth yetkilendirme URL'ini üretir |
| `/api/tiktok/oauth/callback` | GET | TikTok OAuth callback'ini işler ve sonucu frontend hesaplar sayfasına yönlendirir |
| `/api/youtube/oauth/url` | GET | YouTube/Google OAuth yetkilendirme URL'i üretir |
| `/api/youtube/oauth/callback` | GET | YouTube OAuth callback'ini işler ve sonucu frontend hesaplar sayfasına yönlendirir |

## Bilinen Sorunlar / Takip Edilecekler
- OAuth callback'leri, yapılandırılan `FRONTEND_BASE_URL` üzerinden hesaplar sayfasına başarı/hata parametreleriyle yönlendirir.
- `SocialMediaPlatformController` ve `SocialMediaVendorController` şu an tamamen devre dışı (yorum satırı).
