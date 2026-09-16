import{requestApi}from'$lib/api-client';
import{getAuthInitials,type AuthUser,type UserRole}from'$lib/types/auth';

type CurrentUserResponse={
	id:number;
	name:string;
	email:string;
	role:UserRole;
};

export async function getCurrentUser():Promise<AuthUser>{
	const user=await requestApi<CurrentUserResponse>('/api/v1/auth/me');
	return{
		id:user.id,
		name:user.name,
		email:user.email,
		role:user.role,
		initials:getAuthInitials(user.name)
	};
}
