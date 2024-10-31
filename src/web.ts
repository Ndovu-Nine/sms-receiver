import { WebPlugin } from '@capacitor/core';

import type { SmsReceiverPlugin } from './definitions';

export class SmsReceiverWeb extends WebPlugin implements SmsReceiverPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
