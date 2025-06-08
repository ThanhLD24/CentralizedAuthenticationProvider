import dayjs from 'dayjs/esm';

import { ITokenHistory, NewTokenHistory } from './token-history.model';

export const sampleWithRequiredData: ITokenHistory = {
  id: 18799,
};

export const sampleWithPartialData: ITokenHistory = {
  id: 3619,
  hashedToken: 'that',
};

export const sampleWithFullData: ITokenHistory = {
  id: 28295,
  hashedToken: 'abandoned',
  createdDate: dayjs('2025-06-05T17:19'),
  updatedDate: dayjs('2025-06-05T22:17'),
  status: 'INACTIVE',
};

export const sampleWithNewData: NewTokenHistory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
