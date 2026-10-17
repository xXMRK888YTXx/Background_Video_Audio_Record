<p align="center">
  <img src="assets/logo.png" width="128" height="128" alt="Background Video & Audio Record Logo" />
</p>

<h1 align="center">✨ Background Video & Audio Record ✨</h1>
<h3 align="center">🔮 Кодовое имя: <em>Faraday</em> (v1.2.0r — Stealth & Reliability) 🪄</h3>

<p align="center">
  <b>Скрытая, надежная и высокопроизводительная фоновая запись видео и аудио для Android с аппаратной поддержкой двух камер</b><br>
  <i>Stealthy, reliable, and high-performance background video and audio recording for Android with hardware dual-camera support</i>
</p>

<p align="center">
  <a href="https://github.com/KoksMen/Background_Video_Audio_Record/releases"><img src="https://img.shields.io/badge/Version-v1.2.0r--Faraday-7c4dff?style=for-the-badge&logo=github&logoColor=white" alt="Version v1.2.0r-Faraday" /></a>
  <img src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin 2.2" />
  <img src="https://img.shields.io/badge/Compose-Material%203%20Expressive-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose Material 3 Expressive" />
  <img src="https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%E2%80%9336)-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 8.0+" />
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPL--3.0-orange?style=for-the-badge" alt="License GPL-3.0" /></a>
</p>

<p align="center">
  <a href="#-background-video--audio-record-на-русском"><b>🇷🇺 Перейти к русской версии</b></a> •
  <a href="#-background-video--audio-record-in-english"><b>🇬🇧 Switch to English Version</b></a>
</p>

<p align="center">
  <a href="https://github.com/KoksMen/Background_Video_Audio_Record/releases/latest">
    <img src="https://img.shields.io/badge/Скачать%20APK-Download%20APK-4CAF50?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" />
  </a>
  <a href="https://github.com/KoksMen/Background_Video_Audio_Record/releases">
    <img src="https://img.shields.io/badge/GitHub-Releases-181717?style=for-the-badge&logo=github&logoColor=white" alt="Releases" />
  </a>
  <a href="https://github.com/xXMRK888YTXx/Background_Video_Audio_Record">
    <img src="https://img.shields.io/badge/Upstream-Repository-0969DA?style=for-the-badge&logo=git&logoColor=white" alt="Upstream Repository" />
  </a>
</p>

---

# 🇷🇺 Background Video & Audio Record (На русском)

## 💡 О проекте

**Background Video & Audio Record** — это профессиональное мобильное приложение для Android, спроектированное для скрытой, непрерывной и стабильной видео- и аудиозаписи в фоновом режиме.

Приложение способно продолжать высококачественную запись даже при выключенном или заблокированном экране смартфона, при переключении между другими приложениями или во время игр. Развитая система маскировки уведомлений, защита PIN-кодом с биометрией, аппаратный запуск без включения экрана, контроль аудиофокуса звонков и аппаратная поддержка склейки двух камер делают приложение надежным инструментом для фиксации важных событий и обеспечения личной безопасности. 🚀

> [!TIP]
> Приложение полностью автономно, не содержит скрытого сбора телеметрии и сохраняет все медиафайлы локально во внутреннем зашифрованном хранилище устройства с возможностью экспорта в галерею.

---

## 🌟 Ключевые возможности и функционал системы


### 🛡️ 2. Безопасность экрана и защита Recent Apps (`FLAG_SECURE`)
- **Динамический `FLAG_SECURE`:** При активном App Lock окну приложения выставляется флаг безопасности, предотвращающий создание скриншотов или запись экрана посторонними приложениями.
- **Защита списка недавних приложений:** При сворачивании в фон (`onStop`) снимок окна в системном диспетчере задач Android заменяется на защищенный черный экран.

### 🔋 3. Защита от засыпания процессора (`PARTIAL_WAKE_LOCK`)
- **Непрерывная работа в Doze Mode:** Сервисы `RecordVideoService` и `AudioRecordService` удерживают `PowerManager.WakeLock` с таймаутом безопасности на 6 часов.
- **Стабильный захват медиа:** Процессор не приостанавливает выполнение потоков при заблокированном экране, устраняя заикания звука и выпадение кадров. Корректно освобождается при паузе, остановке и уничтожении службы.

### 💾 4. Контроль свободного места на накопителе (`freeSpace` / `StatFs`)
- **Превентивная проверка перед стартом:** Если доступно менее 200 МБ для видео или менее 50 МБ для аудио, запись не стартует вхолостую и отправляет понятное уведомление в шторку.
- **Защита файлов от повреждения во время записи:** Если память устройства опускается ниже критической отметки (100 МБ для видео, 20 МБ для аудио), служба корректно останавливает съемку, сохраняет контейнер MP4/AMR целым и информирует пользователя.

### 📞 5. Умная обработка входящих звонков (`Audio Focus`)
- **Слушатель аудиофокуса (`AudioManager.OnAudioFocusChangeListener`):** При входящем сотовом звонке или вызове в мессенджере (Telegram, WhatsApp) запись автоматически ставится на паузу, предотвращая конфликт за микрофон.
- **Автоматическое возобновление:** После завершения звонка фоновая запись автоматически продолжается без потери уже записанного материала.

### 🎬 6. Экран встроенного видеоплеера (`VideoPlayerScreen`)
- **Material 3 Топ-бар:** Верхняя полупрозрачная плашка с градиентом, огибающая вырез фронтальной камеры (`statusBarsPadding`).
- **Кнопка «Назад»:** Быстрый возврат в медиатеку в один тап.
- **Имя файла и заголовок:** Четкое отображение названия просматриваемой записи.
- **Кнопка «Поделиться»:** Прямая отправка файла в Telegram, WhatsApp или облако (`Intent.ACTION_SEND`).
- **Скрытие по тапу:** Оверлей плавно скрывается и появляется при нажатии на видео.

### 🔐 7. Защита приложения и биометрия (App Lock)
- **PIN-код с криптографическим солением:** Хранение хеша по алгоритму **SHA-256** с индивидуальной солью (`FaradaySec_2026_Salt`) в `DataStore Preferences`.
- **Биометрическая аутентификация (BiometricPrompt):** Мгновенная разблокировка по отпечатку пальца или распознаванию лица.
- **Анимированный экран блокировки:** Точки ввода с защитой от перебора (shake-анимация и красная индикация при ошибке).

### 📸 8. Аппаратная двухкамерная запись (Dual Video & MediaCodec HW Acceleration)
- **Одновременная съемка (Concurrent Camera):** Запись видео сразу с двух камер (основной тыловой и фронтальной).
- **Аппаратное кодирование `DualVideoCombiner`:** Поиск аппаратных кодеков SoC (`c2.qti`, `c2.exynos`, `c2.mtk`, `omx`) через `MediaCodecList` в режиме VBR. Склейка ускорена в 3–5 раз без нагрева смартфона.
- **Индивидуальная компоновка PiP:** Выбор основной камеры и угла отображения миниатюры.

### ⏳ 9. Автоматическая нарезка видео по лимитам (Auto-Split Video)
- **Поддержка FAT32 и SD-карт:** Лимит **4 ГБ / 30 минут** предотвращает повреждение файлов при записи на съемные карты памяти.
- **Бесшовная смена фрагментов (`rotateChunk`):** Ротация файлов без остановки сервиса и без сброса общего таймера.

### 🎛️ 10. Быстрый тайл в шторке (Quick Settings Tile)
- **Плитка быстрого доступа:** Старт и остановка скрытой видеозаписи прямо из шторки Android в один тап с двусторонней синхронизацией статуса.

### 🔔 11. Кастомизация уведомлений и OEM-совместимость
- **Маскировка уведомлений:** Смена иконки и заголовка под нейтральные системные утилиты.
- **OEM Notification Fix:** Устранено кэширование иконок на Xiaomi MIUI/HyperOS, Samsung OneUI, Huawei EMUI и BBK (ColorOS).

---

## 🛠️ Стек технологий

| Категория | Технологии |
| :--- | :--- |
| **Язык разработки** | Kotlin 2.2 (KSP 2.2, Coroutines 1.10, Flows, Channels) |
| **Пользовательский интерфейс** | Jetpack Compose, Material 3 Expressive, Navigation Compose |
| **Камера и медиа** | CameraX 1.5 (VideoCapture, ConcurrentCamera), MediaCodec, MediaRecorder, ExoPlayer 1.8 |
| **Архитектура** | Multi-Module Clean Architecture, MVVM + MVI-like StateFlows |
| **Внедрение зависимостей** | Dagger 2 |
| **Локальные данные** | Room Database (KSP), DataStore Preferences |
| **Безопасность** | AndroidX Biometric, WindowManager FLAG_SECURE, SHA-256 Key Salting |
| **Системные сервисы** | Foreground Service, SensorManager, AudioFocus, Quick Settings Tile |

---

## 📁 Модульная архитектура проекта

```text
├── app/                              # Главный модуль сборки, Dagger DI, AppLock, MainActivity
├── CoreAndroid/                      # Базовые утилиты, NotificationHelper, расширения context
├── CoreCompose/                      # Общие Compose-компоненты, дизайн-система, темы
├── RecordVideoScreen/                # Экран управления записью видео и видоискатель
├── RecordAudioScreen/                # Экран управления звукозаписью
├── RecordVideoService/               # Фоновый Foreground-сервис видеозаписи с CameraX, WakeLock, AudioFocus
├── AudioRecordService/               # Фоновый Foreground-сервис аудиозаписи, WakeLock, AudioFocus
├── VideoRecorder/                    # Обертка над CameraX VideoCapture и Recorder API
├── CameraPreviewCompose/             # Виджет видоискателя камеры для Compose (вкл. PiP)
├── FastOpenAppQuickSettingsService/  # Системная плитка Quick Settings Tile для шторки
├── StorageScreen/                    # Экран управления сохраненными записями (M3 карточки)
├── VideoPlayerScreen/                # Встроенный видеоплеер с Material 3 оверлеем и кнопкой Share
├── AudioPlayer/                      # Сервис и контроллер воспроизведения аудио
├── SettingsScreen/                   # Экран настроек, биометрии, аппаратных триггеров и нарезки видео
├── PreferencesStorage/               # Хранилище настроек на базе Jetpack DataStore
└── AutoExportToExternalStorageScreen/# Модуль автоматического экспорта в общую память
```

---

## 🚀 Установка и системные требования

- **Минимальная версия:** Android 8.0 (API Level 26)
- **Целевая версия:** Android 15 / 16 (API Level 36)
- **Аппаратные требования:** Камера и микрофон. Для режима Dual Video требуется поддержка `FEATURE_CAMERA_CONCURRENT`.

[**⬇️ Скачать официальный APK (Релизы GitHub)**](https://github.com/KoksMen/Background_Video_Audio_Record/releases/latest)

---

# 🇬🇧 Background Video & Audio Record (In English)

## 💡 About The Project

**Background Video & Audio Record** is an advanced open-source Android application engineered for stealthy, reliable, and uninterrupted background video and voice recording.

Featuring customized notification cloaking, biometric app security (PIN + Fingerprint), hardware-accelerated dual-camera picture-in-picture recording, audio focus call management, and low-storage guards, this tool is ideal for documenting crucial moments and ensuring personal security.

---

## 🌟 Key Features

### 🛡️ 2. App Lock Screen Protection (`FLAG_SECURE`)
- Blocks screenshots and obscures recent app task snapshots with a protected black screen when App Lock is active.

### 🔋 3. Uninterrupted Recording with `PARTIAL_WAKE_LOCK`
- Prevents CPU sleep in Android Doze mode when the screen is locked, ensuring seamless 60/30fps capture without dropped frames.

### 💾 4. Dynamic Disk Space Guard (`freeSpace` / `StatFs`)
- Pre-flight check (<200MB video / <50MB audio) and active recording safety threshold (<100MB video / <20MB audio) ensures MP4/AMR containers are properly finalized without corrupting files.

### 📞 5. Intelligent Call & Audio Focus Handling
- Automatically pauses recording when a phone or messenger call begins, and seamlessly resumes when the call ends.

### 🎬 6. Enhanced Video Player Screen (`VideoPlayerScreen`)
- Material 3 top bar with edge-to-edge status bar insets, one-tap Back navigation, video filename display, and instant Share action.

### 🔐 7. App Lock & Biometrics
- Salted SHA-256 PIN code, fingerprint and face authentication via AndroidX `BiometricPrompt`.

### 📸 8. Hardware-Accelerated Dual Video Recording
- Concurrent Camera recording with `MediaCodec` hardware acceleration (AVC/HEVC) for fast, cool PiP encoding.

### ⏳ 9. Automatic Video Chunk Splitting
- FAT32-safe **4 GB / 30-minute** limits with seamless `rotateChunk` rotation.

### 🎛️ 10. Quick Settings Tile
- One-tap toggle directly from the Android notification quick settings shade.

---

## 📄 License

This project is distributed under the **GNU General Public License v3.0 (GPL-3.0)**. See [LICENSE](LICENSE) for details.
