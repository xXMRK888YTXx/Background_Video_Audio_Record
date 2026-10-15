<p align="center">
  <img src="assets/logo.png" width="128" height="128" alt="Background Video & Audio Record Logo" />
</p>

<h1 align="center">✨ Background Video & Audio Record ✨</h1>
<h3 align="center">🔮 Кодовое имя: <em>Faraday</em> (v1.1.0r) 🪄</h3>

<p align="center">
  <b>Скрытая, надежная и высокопроизводительная фоновая запись видео и аудио для Android с аппаратной поддержкой двух камер</b><br>
  <i>Stealthy, reliable, and high-performance background video and audio recording for Android with hardware dual-camera support</i>
</p>

<p align="center">
  <a href="https://github.com/KoksMen/Background_Video_Audio_Record/releases"><img src="https://img.shields.io/badge/Version-v1.1.0r--Faraday-7c4dff?style=for-the-badge&logo=github&logoColor=white" alt="Version v1.1.0r-Faraday" /></a>
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

Приложение способно продолжать высококачественную запись даже при выключенном или заблокированном экране смартфона, при переключении между другими приложениями или во время игр. Развитая система маскировки уведомлений, защита PIN-кодом с биометрией и аппаратная поддержка склейки двух камер делают приложение незаменимым инструментом для фиксации важных жизненных событий, лекций, интервью и обеспечения личной безопасности. 🚀

> [!TIP]
> Приложение полностью автономно, не содержит скрытого сбора телеметрии и сохраняет все медиафайлы локально во внутреннем зашифрованном хранилище устройства с возможностью экспорта в галерею.

---

## 🌟 Ключевые возможности и функционал системы

### 🔐 1. Защита приложения и биометрия (App Lock)
- **PIN-код с криптографическим солением:** Хранение хеша по алгоритму **SHA-256** с индивидуальной солью (`FaradaySec_2026_Salt`) в `DataStore Preferences`.
- **Биометрическая аутентификация (BiometricPrompt):** Мгновенная разблокировка по отпечатку пальца или распознаванию лица (Class 3 Strong Biometrics).
- **Эргономичный экран блокировки:** Анимированные точки ввода с защитой от подбора (shake-анимация и красная индикация при ошибке), тактильный виброотклик.
- **Автоматическая блокировка сессии:** Приложение мгновенно скрывает контент и блокируется при сворачивании (`onStop`) или переключении задач в Android Recents.

### 📸 2. Аппаратная двухкамерная запись (Dual Video & MediaCodec HW Acceleration)
- **Одновременная съемка (Concurrent Camera):** Запись видео сразу с двух камер смартфона (основной тыловой и фронтальной селфи).
- **Аппаратное кодирование `DualVideoCombiner`:** Автоматический поиск и использование аппаратных кодеков SoC (`c2.qti`, `c2.exynos`, `c2.mtk`, `omx`) через `MediaCodecList` в режиме VBR (Variable Bitrate). Склейка двух потоков происходит на аппаратном уровне с ускорением в 3–5 раз без перегрева устройства.
- **Программный fallback:** При отсутствии поддержки аппаратного кодека на старых процессорах система прозрачно переключается на стандартный кодек.
- **Индивидуальная компоновка PiP (Картинка в Картинке):** Возможность выбора, какая камера является основной (фоновой), а какая миниатюрой, с настройкой угла отображения (правый верхний, левый верхний и т.д.).

### ⏳ 3. Автоматическая нарезка видео по лимитам (Auto-Split Video)
- **Поддержка FAT32 и SD-карт:** Лимит **4 ГБ / 30 минут** предотвращает повреждение файлов при записи на съемные карты памяти с ограничением 4 ГБ на файл.
- **Гибкие профили нарезки:** На выбор доступны лимиты: *Без разделения*, *15 минут*, *30 минут*, *60 минут*, *4 ГБ / 30 мин*.
- **Бесшовная смена фрагментов (`rotateChunk`):** Предыдущий фрагмент сохраняется в репозиторий, а новый файл начинается мгновенно. Таймер записи в шторке и интерфейсе не прерывается и накапливает общее время.

### 🎛️ 4. Быстрый тайл в шторке (Quick Settings Tile)
- **Плитка быстрого доступа:** Сервис `FastOpenAppQuickSettingsService` интегрирован прямо в шторку переключателей Android.
- **Старт в один тап:** Запуск скрытой видеозаписи без предварительного открытия интерфейса приложения.
- **Двусторонняя реактивность:** Тайл динамически отображает текущий статус (`STATE_ACTIVE` — *«Идёт запись» / «Нажмите для остановки»*; `STATE_INACTIVE` — *«Быстрая запись» / «Нажмите для старта»*). Синхронизация работает независимо от того, откуда начата запись.

### 🔔 5. Кастомизация уведомлений и OEM-совместимость
- **Маскировка уведомлений:** Возможность смены текста и иконки уведомления под нейтральные сервисы (Калькулятор, Плеер, Загрузка, Синхронизация, Батарея).
- **OEM Notification Fix (`NotificationHelper`):** Устранено кэширование иконок на прошивках Xiaomi (MIUI / HyperOS), Samsung (OneUI), Huawei (EMUI) и BBK (ColorOS / OxygenOS). Использование динамических ID каналов и растровых `LargeIcon` предотвращает подмену иконки на лаунчер вендором.

### 🎨 6. Интерфейс Material 3 Expressive и переработанное хранилище
- **Новый экран записей (Storage):**
  - **Тап по всей карточке:** Для воспроизведения достаточно коснуться любой части карточки — целиться в мелкие кнопки больше не требуется.
  - **Медиа-бейдж:** Цветной скругленный контейнер с градиентом (индиго для видео, фиолетово-бирюзовый для двух камер, изумрудный для аудио).
  - **Эргономичная панель действий:** Выделенная крупная кнопка Play (46dp) под палец, а кнопка «Удалить» отведена в правый угол с защитной красной подсветкой для исключения случайных удалений.
- **HUD записи:** Пульсирующий индикатор статуса записи, переключатель превью/экран, эргономичная зона управления под большой палец.

### 📐 7. Нативный Edge-to-Edge и повышенная надежность
- **Настоящий Edge-to-Edge:** Удален устаревший `Accompanist SystemUIController`. Приложение нативно адаптировано под вырезы, жестовую панель и `WindowInsets` Android 14/15/16.
- **Устранение краша разрешений:** Переход на `fragment-ktx:1.8.6` устранил древний 16-битный баг `startActivityForResult` при запросе разрешений в Activity Result API.
- **Стабильный старт записи:** Устранена гонка состояний между UI CameraPreview и фоновым сервисом, добавлены таймаут-watchdog и корректные Foreground Service Types (`camera|microphone`).

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
| **Безопасность** | AndroidX Biometric, SHA-256 Key Salting, Hardware KeyStore |
| **Системные сервисы** | Foreground Service (Camera/Mic), Quick Settings TileService, BroadcastReceivers |

---

## 📁 Модульная архитектура проекта

```text
├── app/                              # Главный модуль сборки, Dagger DI, AppLock, MainActivity
├── CoreAndroid/                      # Базовые утилиты, NotificationHelper, расширения context
├── CoreCompose/                      # Общие Compose-компоненты, дизайн-система, темы
├── RecordVideoScreen/                # Экран управления записью видео и видоискатель
├── RecordAudioScreen/                # Экран управления звукозаписью
├── RecordVideoService/               # Фоновый Foreground-сервис видеозаписи с CameraX
├── AudioRecordService/               # Фоновый Foreground-сервис аудиозаписи
├── VideoRecorder/                    # Обертка над CameraX VideoCapture и Recorder API
├── CameraPreviewCompose/             # Виджет видоискателя камеры для Compose (вкл. PiP)
├── FastOpenAppQuickSettingsService/  # Системная плитка Quick Settings Tile для шторки
├── StorageScreen/                    # Экран управления сохраненными записями (M3 карточки)
├── VideoPlayerScreen/                # Встроенный видеоплеер
├── AudioPlayer/                      # Сервис и контроллер воспроизведения аудио
├── SettingsScreen/                   # Экран настроек, биометрии и разделения видео
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

The application allows you to record high-resolution video and crystal-clear audio even when your device screen is locked or turned off, when switching between other apps, or during heavy multitasking. Featuring customized notification cloaking, biometric app security (PIN + Fingerprint), hardware-accelerated dual-camera picture-in-picture recording, and a Quick Settings tile, this tool is ideal for documenting crucial moments, meetings, and personal security.

---

## 🌟 Key Features

### 🔐 1. App Lock & Biometrics
- **Salted SHA-256 PIN:** Secure PIN code hashing with unique cryptographic salt (`FaradaySec_2026_Salt`) stored in Jetpack DataStore.
- **Biometric Authentication:** High-security fingerprint and facial recognition via modern AndroidX `BiometricPrompt`.
- **Intelligent Session Locking:** Automatically locks down when the app is placed into background (`onStop`) or viewed in Android Recents.

### 📸 2. Hardware-Accelerated Dual Video Recording
- **Concurrent Camera Support:** Simultaneous recording from front and back cameras.
- **MediaCodec Hardware Acceleration:** `DualVideoCombiner` queries device hardware encoders (`c2.qti`, `c2.exynos`, `c2.mtk`, `omx`) for fast, power-efficient PiP rendering.
- **Automatic Fallback:** Gracefully degrades to standard software codecs if hardware codecs are occupied or unavailable.
- **Customizable PiP Layout:** Change main vs thumbnail camera and PiP window placement on the fly.

### ⏳ 3. Automatic Video Chunk Splitting
- **FAT32 & SD Card Safe:** Automatic split limit at **4 GB / 30 minutes** prevents file corruption on external storage cards.
- **Seamless Chunk Rotation:** Recording continues without interruptions, maintaining a continuous duration timer in the notification shade and UI.

### 🎛️ 4. Quick Settings Tile
- **Instant Background Recording:** Start and stop video capture right from the Android notification shade with a single tap.
- **Dynamic State Tracking:** Tile icon and subtitle dynamically toggle between `STATE_ACTIVE` (*"Recording active"*) and `STATE_INACTIVE` (*"Quick record"*).

### 🔔 5. Notification Cloaking & OEM Fix
- **Stealth Profiles:** Masquerade recording notifications as Calculator, Music Player, Download Manager, or Sync Status.
- **OEM Caching Solution:** Uses dynamic notification channel IDs and bitmap `LargeIcon` generation to bypass aggressive launcher icon overriding on Xiaomi MIUI/HyperOS, Samsung OneUI, and Huawei EMUI.

### 🎨 6. Material 3 Expressive UI & Redesigned Storage Cards
- **Tap-to-Play Cards:** Simply tap anywhere on a card to initiate playback.
- **Media Badges:** Distinct gradient badges for Video, Dual Video, and Audio.
- **Thumb-Zone Play Button:** Dedicated 46dp play button on the right edge.
- **Safety Danger Action:** Delete button is isolated in the opposite corner with subtle red danger styling to eliminate accidental deletions.

### 📐 7. True Edge-to-Edge & Hardened Stability
- **Native Window Insets:** Full compliance with Android 15+ edge-to-edge layout standards.
- **Modern Activity Result API:** Upgraded to `fragment-ktx:1.8.6` resolving the legacy 16-bit `startActivityForResult` request code crash.
- **Stabilized Camera Lifecycle:** Eliminated race conditions between Compose preview and background services.

---

## 📄 License

This project is distributed under the **GNU General Public License v3.0 (GPL-3.0)**. See [LICENSE](LICENSE) for details.
