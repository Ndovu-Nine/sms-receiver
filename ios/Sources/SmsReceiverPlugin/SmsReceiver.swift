import Foundation

@objc public class SmsReceiver: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
