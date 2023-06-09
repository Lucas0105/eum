"use client";

import Image from "next/image";
import { useEffect, useState } from "react";
import { useAppSelector } from "@/redux/hooks";
import AddEventModal from "@/components/modals/AddEventModal";
import { removebgApi } from "@/services/removebgApi";

const RemoveBg = () => {
  const [agingImage, setAgingImage] = useState("");
  const [removebgImageUrl, setRemovebgImageUrl] = useState("");
  // 모달관련 usestate
  const [modalOpen, setModalOpen] = useState(false);

  // redux에 있는 선택된 에이징 base64 파일 가져오기
  const agingSelectUrl = useAppSelector(
    (state) => state.addEventReducer.agingselecturl
  );

  useEffect(() => {
    async function fetchData() {
      const response = await removebgApi(agingSelectUrl);
      setAgingImage(agingSelectUrl);
      setRemovebgImageUrl(response.data.data.image_url);
    }
    fetchData();
  }, []);

  const addEventModalOpen = () => {
    setModalOpen(true);
  };

  return (
    <>
      {agingImage && removebgImageUrl && (
        <div className="flex flex-col items-center justify-center w-full h-[92vh]">
          <Image
            className="h-[45vh] border rounded-lg border-brand-blue border-spacing-1 drop-hadow-2xl"
            src={removebgImageUrl}
            alt="removebgimage"
            width={320}
            height={260}
          />
          <button
            className="my-[2vh] bg-brand-blue text-white py-[1.5vh] px-[6vw] rounded-md shadow-xl font-brand-gmarketsans"
            type="button"
            onClick={addEventModalOpen}
          >
            이벤트등록
          </button>
          <AddEventModal
            modalOpen={modalOpen}
            setModalOpen={setModalOpen}
            image={removebgImageUrl}
          />
        </div>
      )}
    </>
  );
};

export default RemoveBg;
