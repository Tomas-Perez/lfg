import {ApiError} from './ApiError';

export class ApiResponse<T> {
  status: string;
  data: T;
  error: ApiError;
}
