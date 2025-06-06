import dayjs from 'dayjs/esm';

import { IAccessToken, NewAccessToken } from './access-token.model';

export const sampleWithRequiredData: IAccessToken = {
  id: 23591,
};

export const sampleWithPartialData: IAccessToken = {
  id: 25615,
};

export const sampleWithFullData: IAccessToken = {
  id: 29007,
  hashedToken: 'beyond to brr',
  createdDate: dayjs('2025-06-06T15:53'),
  updatedDate: dayjs('2025-06-06T08:40'),
  status: 'USED',
};

export const sampleWithNewData: NewAccessToken = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
