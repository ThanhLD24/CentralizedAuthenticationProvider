import dayjs from 'dayjs/esm';

import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 3793,
};

export const sampleWithPartialData: ITransaction = {
  id: 23791,
  status: 25264,
  deviceInfo: 'schlep ugh underneath',
  createdDate: dayjs('2025-06-06T06:30'),
  userId: 21371,
};

export const sampleWithFullData: ITransaction = {
  id: 20285,
  action: 'cloudy',
  status: 24229,
  message: 'kissingly',
  deviceInfo: 'talkative',
  createdDate: dayjs('2025-06-05T19:29'),
  clientIp: 'less',
  requestPath: 'yawn unlike',
  requestMethod: 'condense absentmindedly',
  username: 'grubby boo',
  userId: 5774,
  duration: 30950,
};

export const sampleWithNewData: NewTransaction = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
