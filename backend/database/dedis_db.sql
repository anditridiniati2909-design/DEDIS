-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 18, 2026 at 09:46 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dedis_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `cache`
--

CREATE TABLE `cache` (
  `key` varchar(255) NOT NULL,
  `value` mediumtext NOT NULL,
  `expiration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `cache_locks`
--

CREATE TABLE `cache_locks` (
  `key` varchar(255) NOT NULL,
  `owner` varchar(255) NOT NULL,
  `expiration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `children`
--

CREATE TABLE `children` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `card_code` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `gender` enum('L','P') NOT NULL,
  `birth_date` date NOT NULL,
  `mother_name` varchar(255) NOT NULL,
  `hamlet` varchar(255) DEFAULT NULL,
  `dusun` varchar(255) DEFAULT 'Dusun Bontocinde',
  `is_mbg_recipient` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `children`
--

INSERT INTO `children` (`id`, `card_code`, `name`, `gender`, `birth_date`, `mother_name`, `hamlet`, `dusun`, `is_mbg_recipient`, `created_at`, `updated_at`) VALUES
(1, 'DEDIS-001', 'Aisyah Ayudia', 'P', '2025-09-16', 'Nur Aini', NULL, 'Dusun Bontocinde', 1, '2026-11-16 02:53:49', '2026-11-16 02:53:49'),
(2, 'DEDIS-002', 'Muhammad Rizky', 'L', '2025-03-16', 'Siti Fatimah', NULL, 'Dusun Bontocinde', 0, '2026-11-16 02:53:49', '2026-11-16 02:53:49'),
(6, 'DEDIS-003', 'inu', 'L', '2025-11-17', 'ina', 'Dusun Bontoala', 'Dusun Bontoala', 0, '2026-11-17 08:22:22', '2026-11-17 08:22:22'),
(7, 'DEDIS-004', 'ina', 'L', '2025-11-17', 'una', 'Dusun Bontocinde', 'Dusun Bontocinde', 0, '2026-11-17 08:48:24', '2026-11-17 08:48:24');

-- --------------------------------------------------------

--
-- Table structure for table `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `jobs`
--

CREATE TABLE `jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `queue` varchar(255) NOT NULL,
  `payload` longtext NOT NULL,
  `attempts` tinyint(3) UNSIGNED NOT NULL,
  `reserved_at` int(10) UNSIGNED DEFAULT NULL,
  `available_at` int(10) UNSIGNED NOT NULL,
  `created_at` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `job_batches`
--

CREATE TABLE `job_batches` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `total_jobs` int(11) NOT NULL,
  `pending_jobs` int(11) NOT NULL,
  `failed_jobs` int(11) NOT NULL,
  `failed_job_ids` longtext NOT NULL,
  `options` mediumtext DEFAULT NULL,
  `cancelled_at` int(11) DEFAULT NULL,
  `created_at` int(11) NOT NULL,
  `finished_at` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `measurements`
--

CREATE TABLE `measurements` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `child_id` bigint(20) UNSIGNED NOT NULL,
  `measurement_date` date NOT NULL,
  `age_in_months` int(11) NOT NULL DEFAULT 0,
  `weight_kg` double NOT NULL,
  `height_cm` double NOT NULL,
  `head_circumference_cm` double DEFAULT NULL,
  `measurement_method` enum('standing','recumbent') NOT NULL DEFAULT 'standing',
  `z_score_hfa` double DEFAULT NULL,
  `z_score_wfa` double DEFAULT NULL,
  `z_score_wfh` double DEFAULT NULL,
  `status_hfa` varchar(255) NOT NULL DEFAULT 'normal',
  `status_wfa` varchar(255) NOT NULL DEFAULT 'normal',
  `status_wfh` varchar(255) NOT NULL DEFAULT 'gizi_baik',
  `needs_attention` tinyint(1) NOT NULL DEFAULT 0,
  `attention_reason` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `measurements`
--

INSERT INTO `measurements` (`id`, `child_id`, `measurement_date`, `age_in_months`, `weight_kg`, `height_cm`, `head_circumference_cm`, `measurement_method`, `z_score_hfa`, `z_score_wfa`, `z_score_wfh`, `status_hfa`, `status_wfa`, `status_wfh`, `needs_attention`, `attention_reason`, `created_at`, `updated_at`) VALUES
(1, 1, '2026-10-16', 13, 9.2, 76.5, 45, 'standing', NULL, NULL, NULL, 'normal', 'normal', 'gizi_baik', 0, NULL, '2026-11-16 02:53:49', '2026-11-16 02:53:49'),
(2, 2, '2026-11-16', 20, 9.8, 78, 46.5, 'standing', NULL, NULL, NULL, 'pendek', 'kurang', 'gizi_kurang', 1, 'Tinggi badan di bawah kurva pertumbuhan (Perlu intervensi protein hewani).', '2026-11-16 02:53:49', '2026-11-16 02:53:49');

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `migrations`
--

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
(1, '0001_01_01_000000_create_users_table', 1),
(2, '0001_01_01_000001_create_cache_table', 1),
(3, '0001_01_01_000002_create_jobs_table', 1),
(4, '2026_11_16_031843_create_personal_access_tokens_table', 1),
(5, '2026_11_16_040000_create_children_and_measurements_tables', 1),
(6, '2026_11_16_041000_add_role_and_phone_to_users_table', 1);

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tokenable_type` varchar(255) NOT NULL,
  `tokenable_id` bigint(20) UNSIGNED NOT NULL,
  `name` text NOT NULL,
  `token` varchar(64) NOT NULL,
  `abilities` text DEFAULT NULL,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `personal_access_tokens`
--

INSERT INTO `personal_access_tokens` (`id`, `tokenable_type`, `tokenable_id`, `name`, `token`, `abilities`, `last_used_at`, `expires_at`, `created_at`, `updated_at`) VALUES
(1, 'App\\Models\\User', 1, 'auth_token', '1aba21de2c92e7e434990319bb10d419a4d712d027f8882d07b7954a88f42c9e', '[\"*\"]', NULL, NULL, '2026-11-17 08:22:23', '2026-11-17 08:22:23'),
(2, 'App\\Models\\User', 1, 'auth_token', '12e8a103c792555db69447af2caf11c90e7eb7cba5aad64cd2967aa0e025e28a', '[\"*\"]', NULL, NULL, '2026-11-17 08:22:59', '2026-11-17 08:22:59'),
(3, 'App\\Models\\User', 1, 'auth_token', '9c767002f3878842d05d3521f8d4237863eab52eac35190eeebb2a06e2d104e6', '[\"*\"]', NULL, NULL, '2026-11-17 08:35:45', '2026-11-17 08:35:45'),
(4, 'App\\Models\\User', 2, 'auth_token', '3e18438df0c723b72c194263e7df6c82f0fb9db70a7da1532c06256631ad9985', '[\"*\"]', NULL, NULL, '2026-11-17 08:48:24', '2026-11-17 08:48:24'),
(5, 'App\\Models\\User', 2, 'auth_token', 'ca326e3c765ee8c0bbcfeec695ca78906e9402eb2b01c501e50501a341dc1fd6', '[\"*\"]', NULL, NULL, '2026-11-17 08:48:59', '2026-11-17 08:48:59'),
(6, 'App\\Models\\User', 2, 'auth_token', 'c32f19a73267f40b036a4e02a246c00131ce31f2cb27db7edb2acf7a8a91d6f4', '[\"*\"]', NULL, NULL, '2026-11-17 08:52:29', '2026-11-17 08:52:29'),
(7, 'App\\Models\\User', 2, 'auth_token', '00f6312a67e01e4c7fd4a1e89a73f045e1b99dd724fa038682bed300d1cc3155', '[\"*\"]', NULL, NULL, '2026-11-17 09:02:48', '2026-11-17 09:02:48');

-- --------------------------------------------------------

--
-- Table structure for table `sessions`
--

CREATE TABLE `sessions` (
  `id` varchar(255) NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text DEFAULT NULL,
  `payload` longtext NOT NULL,
  `last_activity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL DEFAULT 'orang_tua',
  `phone` varchar(255) DEFAULT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `remember_token` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `role`, `phone`, `email_verified_at`, `password`, `remember_token`, `created_at`, `updated_at`) VALUES
(1, 'ina', '08135452866@dedis.posyandu', 'orang_tua', '08135452866', NULL, '$2y$12$1JXt0dlGHw.VwzQFCA2SIuhBilGrwHB9HIkha5kQBz/DZbU97HMT2', NULL, '2026-11-17 08:22:22', '2026-11-17 08:22:22'),
(2, 'una', '08123456789@dedis.posyandu', 'orang_tua', '08123456789', NULL, '$2y$12$2yZkOzcg7W1m35yZx3b71ONWMmYtCE83t96u3ESFiSDyJdWrBgdA.', NULL, '2026-11-17 08:48:24', '2026-11-17 08:49:31');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cache`
--
ALTER TABLE `cache`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_expiration_index` (`expiration`);

--
-- Indexes for table `cache_locks`
--
ALTER TABLE `cache_locks`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_locks_expiration_index` (`expiration`);

--
-- Indexes for table `children`
--
ALTER TABLE `children`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `children_card_code_unique` (`card_code`);

--
-- Indexes for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indexes for table `jobs`
--
ALTER TABLE `jobs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `jobs_queue_index` (`queue`);

--
-- Indexes for table `job_batches`
--
ALTER TABLE `job_batches`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `measurements`
--
ALTER TABLE `measurements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `measurements_child_id_foreign` (`child_id`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`),
  ADD KEY `personal_access_tokens_expires_at_index` (`expires_at`);

--
-- Indexes for table `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sessions_user_id_index` (`user_id`),
  ADD KEY `sessions_last_activity_index` (`last_activity`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `children`
--
ALTER TABLE `children`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `jobs`
--
ALTER TABLE `jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `measurements`
--
ALTER TABLE `measurements`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `measurements`
--
ALTER TABLE `measurements`
  ADD CONSTRAINT `measurements_child_id_foreign` FOREIGN KEY (`child_id`) REFERENCES `children` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
