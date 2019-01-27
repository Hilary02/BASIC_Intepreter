# BASIClike_Interpreter
  - 大学講義の課題
  - 目標：BASICライクの言語のインタプリタの作成

## 実行方法
```shell=
javac -encoding UTF-8 nodes/*.java
javac -encoding UTF-8 funcs/*.java
javac -encoding UTF-8 newlang5/*.java
javac -encoding UTF-8 newlang5/Main.java
java newlang5/Main
```

### src/newlang1
  - ファイル読み込み

### src/newlang3
  - LexicalAnalyzer
  - 字句を解析する

### src/newlang4
  - Syntax Analyzer
  - 構文解析木を作成する

### src/newlang5
  - Interpreter
  - インタプリタとして実行できる

### src/nodes
  - 構文解析木の各ノード

### src/funcs
  - 組み込み関数