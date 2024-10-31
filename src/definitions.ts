export interface SmsReceiverPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
