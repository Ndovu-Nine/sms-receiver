export interface SmsMessage {
  sender: string;
  message: string;
  date: number;
}
export interface SmsReceiverPlugin {
  echo(options: { value: string; }): Promise<{ value: string; }>;
  readSmsMessages(): Promise<{ messages: SmsMessage[]; }>;

  // Listener for incoming SMS messages
  addListener(
    eventName: 'onSmsReceived',
    listenerFunc: (data: SmsMessage) => void
  ): Promise<void>;
}
