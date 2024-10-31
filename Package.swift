// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Smsreceiver",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "Smsreceiver",
            targets: ["SmsReceiverPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SmsReceiverPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SmsReceiverPlugin"),
        .testTarget(
            name: "SmsReceiverPluginTests",
            dependencies: ["SmsReceiverPlugin"],
            path: "ios/Tests/SmsReceiverPluginTests")
    ]
)