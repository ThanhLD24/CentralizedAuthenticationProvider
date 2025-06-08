import dayjs from 'dayjs/esm';
import { TokenStatus } from 'app/entities/enumerations/token-status.model';

export interface ITokenHistory {
  id: number;
  hashedToken?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  status?: keyof typeof TokenStatus | null;
}

export type NewTokenHistory = Omit<ITokenHistory, 'id'> & { id: null };
