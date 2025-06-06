import dayjs from 'dayjs/esm';

import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 3793,
};

export const sampleWithPartialData: ITransaction = {
  id: 26480,
  status: 19830,
  deviceInfo: 'angelic polished emulsify',
  createdDate: dayjs('2025-06-05T20:37'),
};

export const sampleWithFullData: ITransaction = {
  id: 20285,
  action: 'cloudy',
  status: 24229,
  message: 'kissingly',
  deviceInfo: 'talkative',
  createdDate: dayjs('2025-06-05T19:29'),
};

export const sampleWithNewData: NewTransaction = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
