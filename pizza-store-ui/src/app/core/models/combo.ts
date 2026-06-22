export interface ComboDetail{
  id: number;
  comboId: number;
  productId: number;
  quantity: number;
  productName: string;
  productImageUrl: string;
}

export interface ComboDetailRequest {
  productId: number;
  quantity: number;
}

export interface Combo {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  price: number;
  isAvailable: boolean;
  isDeleted: boolean;
  createdAt: string;
  details: ComboDetail[];
}

export interface ComboRequest {
  name: string;
  description: string;
  imageUrl: string;
  price: number;
  isAvailable: boolean;
  details: ComboDetailRequest[];
}
