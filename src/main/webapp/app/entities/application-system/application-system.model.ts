import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IApplicationSystem {
  id: number;
  name?: string | null;
  description?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  active?: boolean | null;
  users?: Pick<IUser, 'id' | 'login'>[] | null;
}

export type NewApplicationSystem = Omit<IApplicationSystem, 'id'> & { id: null };
