import { registerPlugin } from '@capacitor/core';

import type { SmsReceiverPlugin } from './definitions';

const SmsReceiver = registerPlugin<SmsReceiverPlugin>('SmsReceiverPlugin', {
  web: () => import('./web').then((m) => new m.SmsReceiverWeb()),
});

export * from './definitions';
export { SmsReceiver };