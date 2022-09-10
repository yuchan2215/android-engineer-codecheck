# 株式会社ゆめみ Android エンジニアコードチェック課題

## アプリ仕様

本アプリは GitHub のリポジトリを検索するアプリです。

### 環境

- IDE：Android Studio Chipmunk | 2021.2.1 Patch1
- Kotlin：1.5.31
- Java：11
- Gradle：7.3.3
- minSdk：23
- targetSdk：32

### フォーマット

このリポジトリは`lint`と`ktlint`が導入されています。

lintについて
`./gradlew lint` : チェックを行います。

ktlintについて
`./gradlew ktlintCheck` : Linterがチェックします
`./gradlew ktlintFormat` : Linterがフォーマットします
`./gradlew addKtlintCheckGitPreCommitHook` : コミット前にフォーマットされているかチェックするようにします。
`./gradlew addKtlintFormatGitPreCommitHook` : コミット前にフォーマットを実行します

### 動作

1. 何かしらのキーワードを入力
2. (省略可)フローティングボタンから検索設定を開き、検索条件、並び順のタイプ、並び順を選択
3. GitHub API（`search/repositories`）でリポジトリを検索し、結果一覧を概要（リポジトリ名）で表示
4. 特定の結果を選択したら、該当リポジトリの詳細（リポジトリ名、オーナーアイコン、プロジェクト言語、Star 数、~~Watcher 数~~、Fork 数、Issue 数）を表示
5. ユーザー名をタップすると、ユーザーの詳細を表示


### 追加した機能
[こちらのGoogleDrive](https://drive.google.com/file/d/1JvIn5flqgN4IVxBj0QXLq4iIcxfSHwEX/view?usp=sharing)にスライドを作成しました。


# 振り返り
[こちらのMarkdownに書きました。](FURIKAERI.md)