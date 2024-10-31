import { registerPlugin } from '@capacitor/core';

import type { SmsReceiverPlugin } from './definitions';

const SmsReceiver = registerPlugin<SmsReceiverPlugin>('SmsReceiver', {
  web: () => import('./web').then((m) => new m.SmsReceiverWeb()),
});

export * from './definitions';
export { SmsReceiver };
