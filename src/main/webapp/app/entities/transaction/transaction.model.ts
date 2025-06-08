import dayjs from 'dayjs/esm';

export interface ITransaction {
  id: number;
  action?: string | null;
  status?: number | null;
  message?: string | null;
  deviceInfo?: string | null;
  createdDate?: dayjs.Dayjs | null;
  clientIp?: string | null;
  requestPath?: string | null;
  requestMethod?: string | null;
  username?: string | null;
  userId?: number | null;
  duration?: number | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
