# DesktopOnAndroid

## 概要
シンプルなホームアプリです。画面は上下左右斜め自由に移動でき、その中の好きな位置にランチャー用のアイコンを設置することができます。

## Google Play URL

- https://play.google.com/store/apps/details?id=com.yoshinori.DesktopOnAndroid

## 制作意図
スマートフォンのホームアプリとして、長らく「ウィジェットボード」というアプリを愛用していました。画面内の好きな位置に、好きなアイコンを、好きなだけ設置できるとても便利なホームアプリだったのですが、
残念ながら公開終了となってしまい、現在では入手することができなくなってしまいました。私はこれを端末機種変更した後に知り途方にくれたのですが、ないなら作ろうの精神で自分用に作り始めたのがきっかけです。
ストアに公開するに当たり、本家よりも便利なものにするのが目標です。

## DEMO
![64b8d1b5142d3f918bddf7d426eb0e51](https://user-images.githubusercontent.com/62142890/104445359-60323000-55dc-11eb-82c2-4ac4e11b1d6e.gif)
![5b2eaa8e759da29ee128b80d25eb927f](https://user-images.githubusercontent.com/62142890/104445384-658f7a80-55dc-11eb-95ef-a9ccf30cf367.gif)
![fd319f09128f6d83d4cf15fbad5ed836](https://user-images.githubusercontent.com/62142890/104445413-6e804c00-55dc-11eb-9b05-080edfc45d02.gif)
![28dac98ddf4041a1804289bcbf659fb5](https://user-images.githubusercontent.com/62142890/104445607-a25b7180-55dc-11eb-8dc9-7283a07334bb.gif)

## 工夫したポイント
- ScrollViewで直接サポートされていない斜めスクロールDiagonalScrollを実現するため、Viewのスクロール動作はOnTouchメソッド及びSimpleGestureListenerクラスのメソッドをOverrideし自前で実装してあります。
- 子viewであるアイコンを画面外にスクロールしようとしたときの動作も、同様にOnTouchメソッドをOverrideして実装してあります。
- 画面上のViewの配置を動的に設定するため、Room永続ライブラリを使用したリソース管理を行っています。

## 使用技術
- Kotlin(Room/Kotlin Coroutin)

## 課題・今後実装したい機能
- ウィジェットへのサポート
- 詳細設定(アイコンの大きさ・起動直後の始点座標の設定等）のサポート
- アプリ一覧選択画面の中間一致による絞り込み機能の実装
- 複数画面切り替え機能の実装
- Viewの拡大・縮小のサポート
- メインスレッドで動かすことができないRoomへの非同期IO処理は、初めてであったため拙い部分も多分にあるかと思います。お気づきの点がございましたらご指摘いただけると幸いです。


