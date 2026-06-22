export interface Category {
  id: number;
  name: string;
  code: string;
  imageUrl: string;
  description: string;
  isActive: boolean;
  isDeleted: boolean;
  createdAt: string;
}

export interface CategoryRequest {
  name: string;
  code: string;
  imageUrl: string;
  description: string;
  isActive: boolean;
}
