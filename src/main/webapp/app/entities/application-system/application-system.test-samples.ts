import dayjs from 'dayjs/esm';

import { IApplicationSystem, NewApplicationSystem } from './application-system.model';

export const sampleWithRequiredData: IApplicationSystem = {
  id: 6832,
};

export const sampleWithPartialData: IApplicationSystem = {
  id: 22174,
  description: 'boo',
  updatedDate: dayjs('2025-06-05T21:41'),
  hashedSecretKey: 'substantial helplessly',
};

export const sampleWithFullData: IApplicationSystem = {
  id: 28860,
  name: 'quarrelsomely',
  description: 'bustling athwart',
  createdDate: dayjs('2025-06-05T19:47'),
  updatedDate: dayjs('2025-06-06T09:03'),
  active: false,
  hashedSecretKey: 'of ick vice',
};

export const sampleWithNewData: NewApplicationSystem = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
