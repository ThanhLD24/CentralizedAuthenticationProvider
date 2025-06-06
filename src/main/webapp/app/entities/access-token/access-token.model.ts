import dayjs from 'dayjs/esm';
import { TokenStatus } from 'app/entities/enumerations/token-status.model';

export interface IAccessToken {
  id: number;
  hashedToken?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  status?: keyof typeof TokenStatus | null;
}

export type NewAccessToken = Omit<IAccessToken, 'id'> & { id: null };
