import { useSelector } from "react-redux"
import { RootState } from "../store/store"
import { useMemo } from "react"

// export const useAuth = () => {
//     const { user } = useSelector((state: RootState) => state.auth)
  
//     return useMemo(() => ({ user }), [user])
//   }