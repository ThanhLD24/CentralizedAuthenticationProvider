import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface ITransaction {
  id: number;
  action?: string | null;
  status?: number | null;
  message?: string | null;
  deviceInfo?: string | null;
  createdDate?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
