# GrimReaper

視界に特定のプレイヤーが入ったら死亡するプラグイン
説明の都合上特定のプレイヤーを死神と呼びます

# 動作環境
- Minecraft 1.16.5
- PaperMC 1.16.5

# コマンド
- grm
    - assign-mode <player1> <player2> ... 

      指定したPlayerを死神にするモード(Player複数選択可能)

    - random-mode

        ランダムに死神をPlayerにするモード、死神の数はConfigで設定可能、一定間隔で死神を更新

    - mode-off

        現在動いているモードを停止

    - config

        ConfigのUsageを出力

        - show

            Configの設定一覧を出力

        - reload

            Configを初期化

        - set <name> <value>

            nameの値をvalueに更新

# Config一覧

| 設定名               | 内容                                          | デフォルト値 |
| -------------------- | --------------------------------------------- | ------------ |
| tick                 | 死神の死亡処理実行間隔 (単位: tick)           | 20           |
| fov                  | 視野角                                        | 90           |
| aspectRatioWide      | アスペクト比の縦                              | 16           |
| aspectRatioHeight    | アスペクト比の横                              | 9            |
| farClipDistance      | ファークリップまでの距離                      | 50           |
| grimReaperNum        | 死神の人数(random-modeで使用)                 | 1            |
| grimReaperUpdateTick | 死神の更新間隔(random-modeで使用、単位: tick) | 600          |