export interface Topping{
  id: number;
  name: string;
  price: number;
  isAvailable: boolean;
  isDeleted: boolean;
  createdAt: string;
}

export interface ToppingRequest{
  name: string;
  price: number;
  isAvailable: boolean;
}
