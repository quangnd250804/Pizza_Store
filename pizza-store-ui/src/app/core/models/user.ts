import { Role } from './role';

export interface UserResponse {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  dob: string;
  active: boolean;
  deleted: boolean;
  createdAt: string;
  roles: Role[];
}

export interface UserCreateRequest {
  username: string;
  password?: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  dob?: string;
  roleName: string;
}
